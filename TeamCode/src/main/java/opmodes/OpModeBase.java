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

import logic.Movement;
import logic.ShotHandler;
import logic.Team;
import logic.action.DriveActions;
import logic.field.ArtifactSequence;
import logic.position.RobotPosition;

import modules.actuator.cannon.Cannon;
import modules.actuator.cannonBuffer.CannonBuffer;
import modules.actuator.cannonBuffer.CannonBuffersHandler;
import modules.actuator.intake.Intake;
import modules.sensor.BatteryMonitor;
import modules.sensor.GamepadController;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

public class OpModeBase extends LinearOpMode {
    protected ElapsedTime runtime;
    protected Telemetry globalTelemetry;

    protected List<LynxModule> hubs;

    protected final Team team;
    protected final boolean shouldResetPose;

    protected RobotPosition robotPosition;
    protected ShotHandler shotHandler;

    protected BatteryMonitor batteryMonitor;

    protected Movement move;
    protected DriveActions driveActions;
    protected GamepadController gamepadController;

    protected Cannon cannon;
    protected CannonBuffersHandler cannonBuffers;

    protected Intake intake;

    protected ArtifactSequence artifactSequence;

    public OpModeBase(Team team, boolean shouldResetPose) {
        this.team = team;
        this.shouldResetPose = shouldResetPose;
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

        robotPosition =
                RobotPosition.getInstance(globalTelemetry, hardwareMap, team, shouldResetPose);
        shotHandler = new ShotHandler(robotPosition, team);

        batteryMonitor = new BatteryMonitor(hardwareMap, globalTelemetry);

        gamepadController = new GamepadController(runtime, gamepad1);

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

        move =
                new Movement(
                        globalTelemetry,
                        robotPosition,
                        team,
                        moveFL,
                        moveFR,
                        moveBL,
                        moveBR,
                        Movement.MovementMode.FIELD_CENTRIC);

        driveActions = new DriveActions(move, robotPosition, team);

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

        gamepadController.update();

        robotPosition.updatePose();
        shotHandler.update();

        cannon.update(shotHandler.getShotMagnitude());

        System.out.println("Robot Pose: " + robotPosition.getPose().toString());
        globalTelemetry.addData(
                "Shooting target distance", shotHandler.getShotMagnitude().toString());
        globalTelemetry.addData("Shooting target angle", shotHandler.getShotAngle().toString());

        if (artifactSequence == null)
            artifactSequence =
                    ArtifactSequence.findCurrentSequence(robotPosition.getLimelightHandler());
        if (artifactSequence != null)
            globalTelemetry.addData("Pattern", artifactSequence.toString());
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
