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

import config.CannonConfig;
import config.FieldConfig;
import config.HardwareConfig;

import logic.Movement;
import logic.Team;
import logic.action.DriveActions;
import logic.field.ArtifactSequence;
import logic.field.PlayingField;
import logic.position.RobotPosition;

import math.Angle;
import math.Distance;
import math.Position2D;
import math.Vector2D;

import modules.actuator.cannon.Cannon;
import modules.actuator.cannonBuffer.CannonBuffer;
import modules.actuator.cannonBuffer.CannonBuffersHandler;
import modules.actuator.intake.Intake;
import modules.sensor.BatteryMonitor;
import modules.sensor.GamepadController;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.List;

public class OpModeBase extends LinearOpMode {
    protected ElapsedTime runtime;
    protected Telemetry globalTelemetry;

    protected List<LynxModule> hubs;

    protected final Team team;
    protected final boolean shouldResetPose;

    protected RobotPosition robotPosition;

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

        Distance targetDistance = PlayingField.distanceToGoal(robotPosition.getPosition(), team);
        cannon.update(targetDistance);

        System.out.println("Robot Pose: " + robotPosition.getPose().toString());
        globalTelemetry.addData("Target Dist", targetDistance.toString());

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

    private Vector2D shootVector() {
        final double g = 9.81; // gravitational acceleration in m/s^2

        Vector2D robotVelocity = robotPosition.getVelocity();
        Angle velocityAngle = robotVelocity.direction();
        Angle correctionAngle = velocityAngle.negate();
        robotVelocity = robotVelocity.rotate(correctionAngle);

        Angle theta = CannonConfig.CANNON_ANGLE;
        Distance cannonTopHeight = CannonConfig.CANNON_TOP_HEIGHT;
        Distance goalHeight = FieldConfig.GOAL_HEIGHT;
        Position2D cannonPos =
                robotPosition
                        .getPose()
                        .addRelative(CannonConfig.CANNON_RELATIVE_POSITION)
                        .toVector2D()
                        .rotate(correctionAngle)
                        .toPosition2D();
        Position2D goalPos =
                PlayingField.goalPos(team).toVector2D().rotate(correctionAngle).toPosition2D();

        Angle phi = cannonPos.angleTo(goalPos);
        Vector2D dHorizontal = cannonPos.subtract(goalPos).toVector2D();
        double dx = dHorizontal.magnitude().toMeters();
        double dy = goalHeight.subtract(cannonTopHeight).toMeters();

        double ballSpeed = dx * Math.sqrt(g / (2 * (dx * theta.tan() - dy)));
        double robotSpeed = robotVelocity.magnitude().toMeters();

        double shootSpeed =
                Math.sqrt(
                        ballSpeed * ballSpeed
                                - 2 * robotSpeed * ballSpeed * phi.cos()
                                + robotSpeed * robotSpeed);
        double shootAngle = Math.atan2(ballSpeed * phi.sin(), ballSpeed * phi.cos() - robotSpeed);

        Distance norm = new Distance(DistanceUnit.METER, shootSpeed);
        Angle argument = new Angle(AngleUnit.RADIANS, shootAngle).subtract(correctionAngle);

        return new Vector2D(norm, argument);
    }
}
