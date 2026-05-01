package opmodes;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
import modules.actuator.cannonBuffer.CannonBufferHandler;
import modules.actuator.intake.Intake;
import modules.sensor.ArtifactMonitor;
import modules.sensor.BatteryMonitor;
import modules.sensor.DistanceSensorMonitor;
import modules.sensor.GamepadController;

import pedropathing.Constants;

import utils.TelemetryHandler;

import java.util.List;

public abstract class OpModeBase extends LinearOpMode {
    protected ElapsedTime runtime;

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
    protected CannonBufferHandler cannonBuffers;

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
        TelemetryHandler.instantiate(telemetry);

        // For better performance, update only once per frame
        hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        runtime = new ElapsedTime();

        robotPosition =
                RobotPosition.getInstance(hardwareMap, team, useFarStartPose, shouldResetPose);
        follower = Constants.createFollower(hardwareMap, robotPosition);
        shotHandler = new ShotHandler(robotPosition, team);

        batteryMonitor = new BatteryMonitor(hardwareMap);

        gamepadController = new GamepadController(runtime, gamepad1);

        DcMotor moveFL = hardwareMap.get(DcMotor.class, HardwareConfig.FRONT_LEFT_MOTOR_ID);
        DcMotor moveFR = hardwareMap.get(DcMotor.class, HardwareConfig.FRONT_RIGHT_MOTOR_ID);
        DcMotor moveBL = hardwareMap.get(DcMotor.class, HardwareConfig.BACK_LEFT_MOTOR_ID);
        DcMotor moveBR = hardwareMap.get(DcMotor.class, HardwareConfig.BACK_RIGHT_MOTOR_ID);

        DcMotorEx cannonLeft =
                hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_LEFT_ID);
        DcMotorEx cannonRight =
                hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_RIGHT_ID);
        DcMotor cannonBufferMotor =
                hardwareMap.get(DcMotor.class, HardwareConfig.CANNON_BUFFER_MOTOR_ID);

        DcMotor intake = hardwareMap.get(DcMotor.class, HardwareConfig.INTAKE_MOTOR_ID);

        this.distanceSensorMonitor = new DistanceSensorMonitor(hardwareMap);
        this.artifactMonitor = new ArtifactMonitor(distanceSensorMonitor);
        this.artifactMonitorThread = new Thread(this.artifactMonitor);
        this.artifactMonitorThread.start();

        move =
                new Movement(
                        robotPosition,
                        shotHandler,
                        team,
                        moveFL,
                        moveFR,
                        moveBL,
                        moveBR,
                        Movement.MovementMode.FIELD_CENTRIC);

        driveActions = new DriveActions(move, robotPosition, team);

        cannon = new Cannon(cannonLeft, cannonRight);

        CannonBuffer cannonBuffer =
                new CannonBuffer(cannonBufferMotor, DcMotorSimple.Direction.REVERSE);
        this.cannonBuffers = new CannonBufferHandler(cannonBuffer);

        this.intake = new Intake(intake);
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
        TelemetryHandler.addData(
                "Shooting target distance", shotHandler.getShotMagnitude().toString());
        TelemetryHandler.addData("Shooting target angle", shotHandler.getShotAngle().toString());

        if (artifactSequence == null)
            artifactSequence =
                    ArtifactSequence.findCurrentSequence(robotPosition.getLimelightHandler());
        if (artifactSequence != null)
            TelemetryHandler.addData("Pattern", artifactSequence.toString());
    }

    protected void log() {
        batteryMonitor.log();
        TelemetryHandler.addData("Team", team);
        TelemetryHandler.addData("Runtime", runtime.seconds());
        TelemetryHandler.update();

        // Log state
        System.out.println("------- Robot State Log -------");
        System.out.println("Cannon State: " + cannon.getCurrentState().toString());
        System.out.println("Cannon Buffers State: " + cannonBuffers.getCurrentState().toString());
        System.out.println("Intake State: " + intake.getCurrentState().toString());
        System.out.println("Move State: " + move.getCurrentState().toString());
        System.out.println("Robot Position: " + robotPosition.getPose().toString());
        System.out.println("-----------------------------------");
    }

    protected void apply(boolean usingFollower) {
        if (usingFollower) follower.update();
        else move.apply();

        intake.apply();

        cannon.apply();
        cannonBuffers.apply();
    }

    protected void apply() {
        apply(false);
    }
}
