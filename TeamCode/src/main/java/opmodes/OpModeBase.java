package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
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
import modules.sensor.ArtifactMonitor;
import modules.sensor.BatteryMonitor;
import modules.sensor.DistanceSensorMonitor;
import modules.sensor.GamepadController;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import pedropathing.Constants;

import java.util.List;

public abstract class OpModeBase extends LinearOpMode {
    protected ElapsedTime runtime;
    protected Telemetry globalTelemetry;

    protected List<LynxModule> hubs;

    protected final Team team;
    protected final boolean useFarStartPose;
    protected final boolean shouldResetPose;

    protected RobotPosition robotPosition;
    protected Follower follower;
    protected ShotHandler shotHandler;

    protected BatteryMonitor batteryMonitor;

    protected Movement move;
    protected DriveActions driveActions;
    protected GamepadController gamepadController;

    protected Cannon cannon;
    protected CannonBuffersHandler cannonBuffers;

    protected Intake intake;

    protected ArtifactSequence artifactSequence;

    protected DistanceSensorMonitor distanceSensorMonitor;
    protected ArtifactMonitor artifactMonitor;
    private Thread artifactMonitorThread;

    public OpModeBase(Team team, boolean useFarStartPose, boolean shouldResetPose) {
        this.team = team;
        this.useFarStartPose = useFarStartPose;
        this.shouldResetPose = shouldResetPose;
    }

    @Override
    public abstract void runOpMode();

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
                RobotPosition.getInstance(
                        globalTelemetry, hardwareMap, team, useFarStartPose, shouldResetPose);
        follower = Constants.createFollower(hardwareMap, robotPosition);
        shotHandler = new ShotHandler(robotPosition, team, globalTelemetry);

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

        this.distanceSensorMonitor = new DistanceSensorMonitor(hardwareMap);
        this.artifactMonitor = new ArtifactMonitor(distanceSensorMonitor);
        this.artifactMonitorThread = new Thread(this.artifactMonitor);
        this.artifactMonitorThread.start();

        move =
                new Movement(
                        globalTelemetry,
                        robotPosition,
                        shotHandler,
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
        robotPosition.start();
    }

    protected void runStop() {
        robotPosition.stop();
        artifactMonitor.stop();
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

    protected void apply(boolean updateMove) {
        if (updateMove) move.apply();

        intake.apply();

        cannon.apply();
        cannonBuffers.apply();
    }

    protected void apply() {
        apply(true);
    }
}
