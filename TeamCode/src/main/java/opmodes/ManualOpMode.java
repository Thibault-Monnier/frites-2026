package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import logic.PlayingField;
import logic.Replayer;
import logic.RobotPosition;
import logic.Sequence;
import logic.Team;

import math.Distance;

import modules.HardwareConstants;
import modules.actuator.Cannon;
import modules.actuator.CannonBuffer;
import modules.actuator.Intake;
import modules.actuator.IntakeSwitcher;
import modules.actuator.Movement;
import modules.actuator.RobotActuatorModule;
import modules.sensor.BatteryMonitor;
import modules.sensor.GamepadController;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class ManualOpMode extends LinearOpMode {
    private final Team team;
    private final boolean replay;
    private final boolean calculatePose;
    private ElapsedTime runtime;
    private Telemetry globalTelemetry;

    private RobotPosition robotPosition;
    private final PlayingField playingField;

    private BatteryMonitor batteryMonitor;

    private GamepadController gamepad;
    private Movement move;

    // Modules
    private Cannon cannon;

    private CannonBuffer cannonBufferLeft;
    private CannonBuffer cannonBufferRight;

    private Intake intake;

    private IntakeSwitcher intakeSwitcher;

    private Sequence sequence;

    private Replayer.Logger replaySaver;

    public ManualOpMode(Team team, boolean replay, boolean calculatePose) {
        this.team = team;
        this.replay = replay;
        this.calculatePose = calculatePose;
        this.playingField = new PlayingField();
    }

    public ManualOpMode(Team team, boolean replay) {
        this(team, replay, true);
    }

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        runtime.reset();

        if (calculatePose) robotPosition.maybeStart();

        while (opModeIsActive()) {
            runStep();
        }

        if (replay) {
            replaySaver.saveAndExit();
        }
    }

    public void initialize() {
        runtime = new ElapsedTime();
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        if (calculatePose)
            robotPosition = RobotPosition.getInstance(globalTelemetry, hardwareMap, team);

        batteryMonitor = new BatteryMonitor(hardwareMap, globalTelemetry);

        gamepad = new GamepadController(runtime, gamepad1);

        Movement.MovementMode movementMode =
                calculatePose
                        ? Movement.MovementMode.FIELD_CENTRIC
                        : Movement.MovementMode.ROBOT_CENTRIC;
        IMU onBoardIMU = hardwareMap.get(IMU.class, HardwareConstants.IMU_ID);
        move =
                new Movement(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, HardwareConstants.FRONT_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, HardwareConstants.FRONT_RIGHT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, HardwareConstants.BACK_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, HardwareConstants.BACK_RIGHT_MOTOR_ID),
                        movementMode,
                        onBoardIMU);

        cannon =
                new Cannon(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, HardwareConstants.CANNON_MOTOR_LEFT_ID),
                        hardwareMap.get(DcMotor.class, HardwareConstants.CANNON_MOTOR_RIGHT_ID));

        cannonBufferLeft =
                new CannonBuffer(
                        globalTelemetry,
                        hardwareMap.get(CRServo.class, HardwareConstants.CANNON_BUFFER_LEFT),
                        DcMotorSimple.Direction.REVERSE);
        cannonBufferRight =
                new CannonBuffer(
                        globalTelemetry,
                        hardwareMap.get(CRServo.class, HardwareConstants.CANNON_BUFFER_RIGHT),
                        DcMotorSimple.Direction.FORWARD);

        intake =
                new Intake(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, HardwareConstants.INTAKE_MOTOR_ID));

        intakeSwitcher =
                new IntakeSwitcher(
                        globalTelemetry,
                        hardwareMap.get(Servo.class, HardwareConstants.INTAKE_SWITCHER_SERVO));

        if (replay) {
            replaySaver = new Replayer.Logger(runtime, new RobotActuatorModule[] {move});
        }
    }

    public void runStep() {
        move.reset();
        gamepad.update();
        if (calculatePose) robotPosition.updatePose();

        if (calculatePose) {
            if (sequence == null) {
                sequence = Sequence.findCurrentSequence(robotPosition.getLimelightHandler());
            }

            if (sequence != null) {
                // Show the current sequence
                globalTelemetry.addData("Pattern", sequence.toString());
            }
        }

        // Translation : unpressed (fast) and pressed (slow)
        move.joystickTranslate(
                gamepad1,
                gamepad.isPressing(GamepadController.Button.LEFT_STICK),
                robotPosition,
                team);

        // Rotation : unpressed (fast) and pressed (slow)
        move.joystickRotate(gamepad1, gamepad.isPressing(GamepadController.Button.RIGHT_STICK));

        /* --- ACTIONS --- */
        Distance targetDistance = new Distance(DistanceUnit.CM, 130); // Default distance
        if (calculatePose)
            targetDistance = playingField.distanceToGoal(robotPosition.getPosition(), team);
        globalTelemetry.addData("target dist", targetDistance.toString());
        cannon.update(targetDistance);

        if (gamepad.isPressed(GamepadController.Button.TRIGGER_LEFT)) cannon.toggle();

        cannonBufferLeft.set(gamepad.isPressing(GamepadController.Button.DPAD_LEFT));
        cannonBufferRight.set(gamepad.isPressing(GamepadController.Button.DPAD_RIGHT));

        if (gamepad.isPressing(GamepadController.Button.TRIGGER_RIGHT)) {
            cannonBufferLeft.on();
            cannonBufferRight.on();
        } else {
            cannonBufferLeft.off();
            cannonBufferRight.off();
        }

        if (gamepad.isPressing(GamepadController.Button.BUMPER_LEFT)) intake.on();
        else intake.off();
        if (gamepad.isPressed(GamepadController.Button.DPAD_UP)) intakeSwitcher.toggle();
        if (gamepad.isPressed(GamepadController.Button.DPAD_DOWN)) intakeSwitcher.center();

        /* --- OPMODE TELEMETRY --- */
        globalTelemetry.addLine("--- MANUAL MODE ---");
        globalTelemetry.addData("Team", team);

        batteryMonitor.log();

        /* --- APPLY --- */
        move.apply();

        intake.apply();
        intakeSwitcher.apply();

        cannon.apply();
        cannonBufferRight.apply();
        cannonBufferLeft.apply();

        globalTelemetry.update();

        /* --- REPLAY --- */
        if (replay) {
            replaySaver.logCurrentState();
        }
    }
}
