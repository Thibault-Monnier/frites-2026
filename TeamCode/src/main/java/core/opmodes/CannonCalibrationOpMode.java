package core.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import core.Constants;
import core.localization.RobotPosition;
import core.logic.Movement;
import core.logic.PlayingField;
import core.math.Distance;
import core.modules.CannonBuffer;
import core.modules.CannonCalibrator;
import core.modules.GamepadController;
import core.modules.Intake;
import core.modules.IntakeSwitcher;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class CannonCalibrationOpMode extends LinearOpMode {
    private final Constants.Team team;
    private ElapsedTime runtime;
    private Telemetry globalTelemetry;

    private RobotPosition robotPosition;
    private final PlayingField playingField;

    private GamepadController gamepad;
    private Movement move;

    private CannonCalibrator cannonCalibrator;

    private CannonBuffer cannonBufferLeft;
    private CannonBuffer cannonBufferRight;

    private Intake intake;

    private IntakeSwitcher intakeSwitcher;

    public CannonCalibrationOpMode(Constants.Team team) {
        this.team = team;
        this.playingField = new PlayingField();
    }

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        runtime.reset();
        robotPosition.maybeStart();

        while (opModeIsActive()) {
            runStep();
        }

        runStop();
    }

    public void initialize() {
        runtime = new ElapsedTime();
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        robotPosition = RobotPosition.getInstance(globalTelemetry, hardwareMap, team);

        gamepad = new GamepadController(runtime, gamepad1);

        IMU onBoardIMU = hardwareMap.get(IMU.class, Constants.IMU_ID);
        move =
                new Movement(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, Constants.FRONT_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.FRONT_RIGHT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.BACK_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.BACK_RIGHT_MOTOR_ID),
                        onBoardIMU);

        cannonCalibrator =
                new CannonCalibrator(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, Constants.CANNON_MOTOR_LEFT_ID),
                        hardwareMap.get(DcMotor.class, Constants.CANNON_MOTOR_RIGHT_ID));

        cannonBufferLeft =
                new CannonBuffer(
                        globalTelemetry,
                        hardwareMap.get(CRServo.class, Constants.CANNON_BUFFER_LEFT),
                        DcMotorSimple.Direction.REVERSE);
        cannonBufferRight =
                new CannonBuffer(
                        globalTelemetry,
                        hardwareMap.get(CRServo.class, Constants.CANNON_BUFFER_RIGHT),
                        DcMotorSimple.Direction.FORWARD);

        intake =
                new Intake(
                        globalTelemetry, hardwareMap.get(DcMotor.class, Constants.INTAKE_MOTOR_ID));

        intakeSwitcher =
                new IntakeSwitcher(
                        globalTelemetry,
                        hardwareMap.get(Servo.class, Constants.INTAKE_SWITCHER_SERVO));
    }

    public void runStep() {
        move.reset();
        gamepad.update();
        robotPosition.updatePose();

        // Translation : unpressed (fast) and pressed (slow)
        move.joystickTranslate(gamepad1, gamepad.isPressing(GamepadController.Button.LEFT_STICK));

        // Rotation : bumpers (fast) and triggers (slow)
        move.bumperTurn(gamepad1);

        /* --- ACTIONS --- */
        Distance targetDistance = playingField.distanceToGoal(robotPosition.getPosition(), team);

        if (gamepad.isPressed(GamepadController.Button.X)) cannonCalibrator.toggle();
        if (gamepad.isPressed(GamepadController.Button.DPAD_UP)) cannonCalibrator.speedup();
        if (gamepad.isPressed(GamepadController.Button.DPAD_DOWN)) cannonCalibrator.slowdown();
        if (gamepad.isPressed(GamepadController.Button.B)) {
            cannonCalibrator.saveCurrentCalibrationData(targetDistance);
            cannonCalibrator.printCalibrationData();
        }

        if (gamepad.isPressing(GamepadController.Button.DPAD_LEFT)) {
            cannonBufferLeft.on();
        } else {
            cannonBufferLeft.off();
        }
        if (gamepad.isPressing(GamepadController.Button.DPAD_RIGHT)) {
            cannonBufferRight.on();
        } else {
            cannonBufferRight.off();
        }

        if (gamepad.isPressed(GamepadController.Button.A)) intake.toggle();

        globalTelemetry.addLine("--- CALIBRATION MODE ---");
        globalTelemetry.addData("Team", team);

        /* --- APPLY --- */
        move.apply();

        intake.apply();
        intakeSwitcher.apply();

        cannonCalibrator.apply();
        cannonBufferRight.apply();
        cannonBufferLeft.apply();

        globalTelemetry.update();
    }

    public void runStop() {
        cannonCalibrator.printCalibrationData();
    }
}
