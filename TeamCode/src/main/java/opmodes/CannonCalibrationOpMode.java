package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import logic.PlayingField;
import logic.position.RobotPosition;
import logic.Team;

import math.Distance;

import config.HardwareConfig;
import modules.actuator.CannonBuffer;
import modules.actuator.CannonCalibrator;
import modules.actuator.Intake;
import modules.actuator.IntakeSwitcher;
import modules.actuator.Movement;
import modules.sensor.BatteryMonitor;
import modules.sensor.GamepadController;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class CannonCalibrationOpMode extends LinearOpMode {
    private final Team team;
    private ElapsedTime runtime;
    private Telemetry globalTelemetry;

    private RobotPosition robotPosition;

    private BatteryMonitor batteryMonitor;

    private GamepadController gamepad;
    private Movement move;

    private CannonCalibrator cannonCalibrator;

    private CannonBuffer cannonBufferLeft;
    private CannonBuffer cannonBufferRight;

    private Intake intake;

    private IntakeSwitcher intakeSwitcher;

    public CannonCalibrationOpMode(Team team) {
        this.team = team;
    }

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        runtime.reset();

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

        batteryMonitor = new BatteryMonitor(hardwareMap, globalTelemetry);

        gamepad = new GamepadController(runtime, gamepad1);

        Movement.MovementMode movementMode = Movement.MovementMode.FIELD_CENTRIC;
        IMU onBoardIMU = hardwareMap.get(IMU.class, HardwareConfig.IMU_ID);
        move =
                new Movement(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, HardwareConfig.FRONT_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, HardwareConfig.FRONT_RIGHT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, HardwareConfig.BACK_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, HardwareConfig.BACK_RIGHT_MOTOR_ID),
                        movementMode,
                        onBoardIMU);

        cannonCalibrator =
                new CannonCalibrator(
                        globalTelemetry,
                        hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_LEFT_ID),
                        hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_RIGHT_ID));

        cannonBufferLeft =
                new CannonBuffer(
                        globalTelemetry,
                        hardwareMap.get(CRServo.class, HardwareConfig.CANNON_BUFFER_LEFT),
                        DcMotorSimple.Direction.REVERSE);
        cannonBufferRight =
                new CannonBuffer(
                        globalTelemetry,
                        hardwareMap.get(CRServo.class, HardwareConfig.CANNON_BUFFER_RIGHT),
                        DcMotorSimple.Direction.FORWARD);

        intake =
                new Intake(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, HardwareConfig.INTAKE_MOTOR_ID));

        intakeSwitcher =
                new IntakeSwitcher(
                        globalTelemetry,
                        hardwareMap.get(Servo.class, HardwareConfig.INTAKE_SWITCHER_SERVO));
    }

    public void runStep() {
        move.reset();
        gamepad.update();
        robotPosition.updatePose();

        if (gamepad.isPressing(GamepadController.Button.BUMPER_LEFT)) {
            // Lock towards the goal
            move.lockedJoystickTranslate(
                    gamepad1,
                    gamepad.isPressing(GamepadController.Button.LEFT_STICK),
                    robotPosition,
                    team,
                    PlayingField.goalPos(team));
        } else {
            move.joystickTranslate(
                    gamepad1,
                    gamepad.isPressing(GamepadController.Button.LEFT_STICK),
                    robotPosition,
                    team);
            move.joystickRotate(gamepad1, gamepad.isPressing(GamepadController.Button.RIGHT_STICK));
        }

        /* --- ACTIONS --- */
        Distance targetDistance = PlayingField.distanceToGoal(robotPosition.getPosition(), team);

        if (gamepad.isPressed(GamepadController.Button.X)) cannonCalibrator.toggle();
        if (gamepad.isPressed(GamepadController.Button.Y)) cannonCalibrator.speedup();
        if (gamepad.isPressed(GamepadController.Button.A)) cannonCalibrator.slowdown();
        if (gamepad.isPressed(GamepadController.Button.B)) {
            cannonCalibrator.saveCurrentCalibrationData(targetDistance);
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

        if (gamepad.isPressed(GamepadController.Button.TRIGGER_LEFT)) intake.toggle();

        globalTelemetry.addLine("--- CALIBRATION MODE ---");
        globalTelemetry.addData("Team", team);
        cannonCalibrator.printCalibrationData();

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
        globalTelemetry.update();
    }
}
