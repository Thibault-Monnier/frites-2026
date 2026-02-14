package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import config.HardwareConfig;

import logic.field.ArtifactSequence;
import logic.action.DriveActions;
import logic.Movement;
import logic.field.PlayingField;
import logic.Team;
import logic.position.RobotPosition;

import math.Distance;

import modules.actuator.Cannon;
import modules.actuator.CannonBuffer;
import modules.actuator.CannonBuffersHandler;
import modules.actuator.Intake;
import modules.sensor.BatteryMonitor;
import modules.sensor.GamepadController;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.List;

public class OpModeBase extends LinearOpMode {
    protected ElapsedTime runtime;
    protected Telemetry globalTelemetry;

    protected List<LynxModule> hubs;

    protected final Team team;
    protected final boolean calculatePose;
    protected final boolean shouldResetPose;

    protected RobotPosition robotPosition;

    protected BatteryMonitor batteryMonitor;

    protected Movement move;
    protected DriveActions driveActions;
    protected GamepadController gamepad;

    protected Cannon cannon;
    protected CannonBuffersHandler cannonBuffers;

    protected Intake intake;

    protected ArtifactSequence artifactSequence;

    public OpModeBase(Team team, boolean shouldResetPose, boolean calculatePose) {
        this.team = team;
        this.shouldResetPose = shouldResetPose;
        this.calculatePose = calculatePose;
    }

    @Override
    public void runOpMode() {
        throw new UnsupportedOperationException("Not implemented in base class");
    }

    protected void initialize() {
        // For better performance, update only once per frame
        hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        runtime = new ElapsedTime();
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        if (calculatePose)
            robotPosition =
                    RobotPosition.getInstance(globalTelemetry, hardwareMap, team, shouldResetPose);

        batteryMonitor = new BatteryMonitor(hardwareMap, globalTelemetry);

        gamepad = new GamepadController(runtime, gamepad1);

        DcMotor moveFL = hardwareMap.get(DcMotor.class, HardwareConfig.FRONT_LEFT_MOTOR_ID);
        DcMotor moveFR = hardwareMap.get(DcMotor.class, HardwareConfig.FRONT_RIGHT_MOTOR_ID);
        DcMotor moveBL = hardwareMap.get(DcMotor.class, HardwareConfig.BACK_LEFT_MOTOR_ID);
        DcMotor moveBR = hardwareMap.get(DcMotor.class, HardwareConfig.BACK_RIGHT_MOTOR_ID);
        DcMotorEx cannonLeft =
                hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_LEFT_ID);
        DcMotorEx cannonRight =
                hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_RIGHT_ID);
        CRServo cannonBufferLeft =
                hardwareMap.get(CRServo.class, HardwareConfig.CANNON_BUFFER_LEFT);
        CRServo cannonBufferRight =
                hardwareMap.get(CRServo.class, HardwareConfig.CANNON_BUFFER_RIGHT);
        DcMotor intake = hardwareMap.get(DcMotor.class, HardwareConfig.INTAKE_MOTOR_ID);

        Movement.MovementMode movementMode =
                calculatePose
                        ? Movement.MovementMode.FIELD_CENTRIC
                        : Movement.MovementMode.ROBOT_CENTRIC;
        move = new Movement(globalTelemetry, moveFL, moveFR, moveBL, moveBR, movementMode);

        driveActions = new DriveActions(robotPosition, team);

        cannon = new Cannon(globalTelemetry, cannonLeft, cannonRight);

        CannonBuffer leftBuffer =
                new CannonBuffer(
                        globalTelemetry, cannonBufferLeft, DcMotorSimple.Direction.REVERSE);
        CannonBuffer rightBuffer =
                new CannonBuffer(
                        globalTelemetry, cannonBufferRight, DcMotorSimple.Direction.FORWARD);
        this.cannonBuffers = new CannonBuffersHandler(leftBuffer, rightBuffer);

        this.intake = new Intake(globalTelemetry, intake);
    }

    protected void runStart() {
        runtime.reset();
    }

    protected void update() {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }

        gamepad.update();

        Distance targetDistance = new Distance(DistanceUnit.CM, 200); // Default distance
        if (calculatePose) {
            robotPosition.updatePose();
            targetDistance = PlayingField.distanceToGoal(robotPosition.getPosition(), team);
        }
        cannon.update(targetDistance);
        System.out.println("Robot Pose: " + robotPosition.getPose().toString());
        globalTelemetry.addData("Target Dist", targetDistance.toString());

        if (calculatePose) {
            if (artifactSequence == null)
                artifactSequence =
                        ArtifactSequence.findCurrentSequence(robotPosition.getLimelightHandler());
            if (artifactSequence != null)
                globalTelemetry.addData("Pattern", artifactSequence.toString());
        }
    }

    protected void log() {
        batteryMonitor.log();
        globalTelemetry.addData("Team", team);
        globalTelemetry.addData("Runtime", runtime.seconds());
        globalTelemetry.update();

        // Log state
        System.out.println("------- Robot State Log -------");
        System.out.println("Cannon State: " + cannon.getCurrentState().toString());
        System.out.println("Cannon Buffers State: " + cannonBuffers.getCurrentState().toString());
        System.out.println("Intake State: " + intake.getCurrentState().toString());
        System.out.println("Move State: " + move.getCurrentState().toString());
        System.out.println("Robot Position: " + robotPosition.getPose().toString());
        System.out.println("-----------------------------------");
    }

    protected void apply() {
        move.apply();

        intake.apply();

        cannon.apply();
        cannonBuffers.apply();
    }
}
