package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import logic.ArtifactSequence;
import logic.DriveActions;
import logic.PlayingField;
import logic.RobotPosition;
import logic.Team;

import math.Distance;

import config.HardwareConfig;
import modules.actuator.Cannon;
import modules.actuator.CannonBuffer;
import modules.actuator.CannonBuffersHandler;
import modules.actuator.Intake;
import modules.actuator.IntakeSwitcher;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import modules.actuator.Movement;

import java.util.ArrayDeque;
import java.util.Deque;

public class AutoOpMode extends LinearOpMode {
    private final Team team;

    private ElapsedTime runtime;
    private Telemetry globalTelemetry;

    private RobotPosition robotPosition;

    private Movement move;
    private DriveActions driveActions;

    private Cannon cannon;
    private CannonBuffersHandler cannonBuffers;

    private Intake intake;
    private IntakeSwitcher intakeSwitcher;

    private ArtifactSequence artifactSequence;

    // private final Deque<Action> actionSequence = new ArrayDeque<>();

    public AutoOpMode(Team team) {
        this.team = team;
    }

    @Override
    public void runOpMode() {
        initialize();
        // initSequence();

        waitForStart();

        runtime.reset();

        double prevTime = runtime.milliseconds();
        while (opModeIsActive() /*&& !actionSequence.isEmpty()*/) {
            // Consistent step duration for better PIDs
            double time = runtime.milliseconds();
            while (time - prevTime < 100) {
                time = runtime.milliseconds();
            }
            prevTime = time;

            runStep();
        }
    }

    private void initialize() {
        runtime = new ElapsedTime();
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        robotPosition = RobotPosition.getInstance(globalTelemetry, hardwareMap, team, true);

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
        Servo intakeSwitcher = hardwareMap.get(Servo.class, HardwareConfig.INTAKE_SWITCHER_SERVO);

        IMU onBoardIMU = hardwareMap.get(IMU.class, HardwareConfig.IMU_ID);
        move =
                new Movement(
                        globalTelemetry,
                        moveFL,
                        moveFR,
                        moveBL,
                        moveBR,
                        Movement.MovementMode.FIELD_CENTRIC,
                        onBoardIMU);

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
        this.intakeSwitcher = new IntakeSwitcher(globalTelemetry, intakeSwitcher);
    }

    /*private void initSequence() {
        registerAction(powerOnCannon());
        registerAction(intakeSwitcherRight());

        shootSequence();

        collectArtifactRowSequence(Artifact.Row.BACK);
        shootSequence();

        registerAction(driveActions.driveToArtifactRowEntryPose(Artifact.Row.MIDDLE));
        registerAction(turnTowardsArtifactRow(Artifact.Row.MIDDLE));

        // collectArtifactRowSequence(Artifact.Row.MIDDLE);
        // shootSequence();

        // registerAction(driveActions.driveToLeavePose());
        // registerAction(turnTowardsLeavePose());
    }

    private void shootSequence() {
        registerAction(driveActions.driveToGoalShootPosition());
        registerAction(turnTowardsGoal());

        registerAction(prepareToShoot());
        registerAction(intakeOn());
        registerAction(shoot());
        registerAction(intakeOff());
    }

    private void collectArtifactRowSequence(Artifact.Row row) {
        registerAction(driveActions.driveToArtifactRowEntryPose(row));
        registerAction(turnTowardsArtifactRow(row));

        registerAction(intakeOn());
        registerAction(driveActions.collectArtifactsFromRow(row));
        registerAction(intakeOff());
        registerAction(driveActions.driveBackToArtifactRowEntryPose(row));
    }

    private Action powerOnCannon() {
        return new SimpleAction(() -> cannon.on());
    }

    private Action prepareToShoot() {
        return new SimpleAction(() -> cannonBuffers.shootReset());
    }

    private Action shoot() {
        return telemetryPacket ->
                !cannonBuffers.shootContinue(
                        intakeSwitcher.getCurrentPosition() == IntakeSwitcher.Position.RIGHT);
    }

    private Action turnTowardsGoal() {
        return telemetryPacket -> move.turnTowards(robotPosition, PlayingField.goalPos(team));
    }

    private Action turnTowardsArtifactRow(Artifact.Row row) {
        return telemetryPacket ->
                move.turnTowardsHeading(
                        robotPosition, PlayingField.artifactRowEntryPose(team, row).getHeading());
    }

    private Action turnTowardsLeavePose() {
        return telemetryPacket ->
                move.turnTowardsHeading(
                        robotPosition, PlayingField.autoModeLeavePose(team).getHeading());
    }

    private Action intakeOn() {
        return new SimpleAction(
                () -> {
                    intake.on();
                    cannonBuffers.reverse();
                });
    }

    private Action intakeOff() {
        return new SimpleAction(
                () -> {
                    intake.off();
                    cannonBuffers.off();
                });
    }

    private Action intakeSwitcherRight() {
        return new SimpleAction(() -> intakeSwitcher.right());
    }

    private Action intakeSwitcherLeft() {
        return new SimpleAction(() -> intakeSwitcher.left());
    }

    private Action intakeSwitcherCenter() {
        return new SimpleAction(() -> intakeSwitcher.center());
    }

    private void registerAction(Action action) {
        actionSequence.addLast(action);
    }*/

    private void runStep() {
        update();

        TelemetryPacket packet = new TelemetryPacket();

        /*Action currentAction = actionSequence.getFirst();

        currentAction.preview(packet.fieldOverlay());

        if (!currentAction.run(packet)) {
            actionSequence.removeFirst();
        }*/

        apply();

        FtcDashboard.getInstance().sendTelemetryPacket(packet);

        globalTelemetry.addLine("--- Main Auto Mode ---");
        globalTelemetry.addData("Runtime", runtime.seconds());
        // globalTelemetry.addData("Ran", currentAction.toString());
        globalTelemetry.update();

        System.out.println("------------------------------------------------ AUTO");
        System.out.println("move status: " + move.getCurrentState().toString());
        System.out.println("cannon status: " + cannon.getCurrentState().toString());
        System.out.println("cannonBuffers status: " + cannonBuffers.getCurrentState().toString());
        System.out.println("intake status: " + intake.getCurrentState().toString());
        System.out.println("intakeSwitcher status: " + intakeSwitcher.getCurrentState().toString());
        System.out.println("------------------------------------------------ AUTO");
    }

    private void update() {
        move.reset();

        robotPosition.updatePose();
        System.out.println("Robot Pose: " + robotPosition.getPose().toString());

        if (artifactSequence == null)
            artifactSequence =
                    ArtifactSequence.findCurrentSequence(robotPosition.getLimelightHandler());
        if (artifactSequence != null)
            globalTelemetry.addData("Pattern", artifactSequence.toString());

        Distance targetDistance = PlayingField.distanceToGoal(robotPosition.getPosition(), team);
        globalTelemetry.addData("Target Dist", targetDistance.toString());
        cannon.update(targetDistance);

        // drive.updatePoseEstimate();
    }

    private void apply() {
        intake.apply();
        intakeSwitcher.apply();

        cannon.apply();
        cannonBuffers.apply();
    }
}
