package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import logic.Artifact;
import logic.ArtifactSequence;
import logic.DriveActions;
import logic.PlayingField;
import logic.RobotPosition;
import logic.SimpleAction;
import logic.Team;

import math.Distance;

import modules.HardwareConstants;
import modules.actuator.Cannon;
import modules.actuator.CannonBuffer;
import modules.actuator.CannonBuffersHandler;
import modules.actuator.Intake;
import modules.actuator.IntakeSwitcher;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import roadrunner.MecanumDrive;

import java.util.ArrayDeque;
import java.util.Deque;

public class AutoOpMode extends LinearOpMode {
    private final Team team;

    private ElapsedTime runtime;
    private Telemetry globalTelemetry;

    private RobotPosition robotPosition;

    private MecanumDrive drive;
    private DriveActions driveActions;

    private Cannon cannon;
    private CannonBuffersHandler cannonBuffers;

    private Intake intake;
    private IntakeSwitcher intakeSwitcher;

    private ArtifactSequence artifactSequence;

    private final Deque<Action> actionSequence = new ArrayDeque<>();

    public AutoOpMode(Team team) {
        this.team = team;
    }

    @Override
    public void runOpMode() {
        initialize();
        initSequence();

        waitForStart();

        runtime.reset();

        double prevTime = runtime.milliseconds();
        while (opModeIsActive() && !actionSequence.isEmpty()) {
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

        robotPosition = RobotPosition.getInstance(globalTelemetry, hardwareMap, team);

        drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        driveActions = new DriveActions(drive, robotPosition, team);

        DcMotorEx cannonLeft =
                hardwareMap.get(DcMotorEx.class, HardwareConstants.CANNON_MOTOR_LEFT_ID);
        DcMotorEx cannonRight =
                hardwareMap.get(DcMotorEx.class, HardwareConstants.CANNON_MOTOR_RIGHT_ID);
        CRServo cannonBufferLeft =
                hardwareMap.get(CRServo.class, HardwareConstants.CANNON_BUFFER_LEFT);
        CRServo cannonBufferRight =
                hardwareMap.get(CRServo.class, HardwareConstants.CANNON_BUFFER_RIGHT);
        DcMotor intake = hardwareMap.get(DcMotor.class, HardwareConstants.INTAKE_MOTOR_ID);
        Servo intakeSwitcher =
                hardwareMap.get(Servo.class, HardwareConstants.INTAKE_SWITCHER_SERVO);

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

    private void initSequence() {
        registerAction(powerOnCannon());

        shootSequence(Math.toRadians(-135));

        collectArtifactRowSequence(Artifact.Row.MIDDLE);
        shootSequence(Math.toRadians(-135));

        collectArtifactRowSequence(Artifact.Row.BACK);
        shootSequence(Math.toRadians(0));

        registerAction(driveActions.driveToLeavePose());
    }

    private void shootSequence(double tangentAngleRadians) {
        registerAction(driveActions.driveToGoalShootPosition(tangentAngleRadians));
        registerAction(prepareToShoot());
        registerAction(shoot());
    }

    private void collectArtifactRowSequence(Artifact.Row row) {
        registerAction(driveActions.driveToArtifactRowEntryPose(row));
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
        return telemetryPacket -> !cannonBuffers.shootContinue();
    }

    private Action intakeOn() {
        return new SimpleAction(() -> intake.on());
    }

    private Action intakeOff() {
        return new SimpleAction(() -> intake.off());
    }

    private void registerAction(Action action) {
        actionSequence.addLast(action);
    }

    private void runStep() {
        update();

        TelemetryPacket packet = new TelemetryPacket();

        Action currentAction = actionSequence.getFirst();

        currentAction.preview(packet.fieldOverlay());

        if (!currentAction.run(packet)) {
            actionSequence.removeFirst();
        }

        globalTelemetry.addLine("--- Main Auto Mode ---");
        globalTelemetry.addData("Runtime", runtime.seconds());
    }

    private void update() {
        robotPosition.updatePose();

        if (artifactSequence == null)
            artifactSequence =
                    ArtifactSequence.findCurrentSequence(robotPosition.getLimelightHandler());
        if (artifactSequence != null)
            globalTelemetry.addData("Pattern", artifactSequence.toString());

        Distance targetDistance = PlayingField.distanceToGoal(robotPosition.getPosition(), team);
        globalTelemetry.addData("Target Dist", targetDistance.toString());
        cannon.update(targetDistance);
    }
}
