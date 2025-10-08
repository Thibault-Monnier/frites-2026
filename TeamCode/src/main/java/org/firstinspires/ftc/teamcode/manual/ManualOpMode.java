package org.firstinspires.ftc.teamcode.manual;

import static org.firstinspires.ftc.teamcode.robot.Constants.BASKET_CLOSED_POSITION;
import static org.firstinspires.ftc.teamcode.robot.Constants.BASKET_ID;
import static org.firstinspires.ftc.teamcode.robot.Constants.BASKET_OPEN_POSITION;
import static org.firstinspires.ftc.teamcode.robot.Constants.PLASTIC_CLAMP_CLOSED_POSITION;
import static org.firstinspires.ftc.teamcode.robot.Constants.PLASTIC_CLAMP_ID;
import static org.firstinspires.ftc.teamcode.robot.Constants.PLASTIC_CLAMP_OPEN_POSITION;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.models.Clamp;
import org.firstinspires.ftc.teamcode.models.RobotModule;
import org.firstinspires.ftc.teamcode.robot.Arm;
import org.firstinspires.ftc.teamcode.robot.Constants;
import org.firstinspires.ftc.teamcode.robot.GamepadController;
import org.firstinspires.ftc.teamcode.robot.Movement;
import org.firstinspires.ftc.teamcode.robot.Replayer;
import org.firstinspires.ftc.teamcode.robot.ServoClamp;

@Config
public class ManualOpMode extends OpMode {
    private final boolean DEBUG = true;

    // Put condition with FEATURE_DISABLED around features that you want to unable/disable
    // during an OpMode
    private final boolean feature_disabled = false;

    private final Constants.Team team;
    private final Constants.StartPosition startPosition;
    private final boolean replay;
    private final Arm.ColorSensorMode colorSensorAutomation;
    private ElapsedTime runtime;
    private GamepadController gamepad;
    private Movement move;
    private Arm arm;
    private Telemetry globalTelemetry;

    private Replayer.Logger replaySaver;

    public ManualOpMode(
            Constants.Team team,
            Constants.StartPosition startPosition,
            boolean replay,
            Arm.ColorSensorMode colorSensorAutomation) {
        this.team = team;
        this.startPosition = startPosition;
        this.replay = replay;
        this.colorSensorAutomation = colorSensorAutomation;
    }

    @Override
    public void init() {
        runtime = new ElapsedTime();

        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        gamepad = new GamepadController(globalTelemetry, runtime, gamepad1);

        IMU onBoardIMU = hardwareMap.get(IMU.class, Constants.IMU_ID);

        move =
                new Movement(
                        globalTelemetry,
                        runtime,
                        hardwareMap.get(DcMotor.class, Constants.FRONT_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.FRONT_RIGHT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.BACK_LEFT_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.BACK_RIGHT_MOTOR_ID),
                        onBoardIMU);

        arm =
                new Arm(
                        globalTelemetry,
                        hardwareMap.get(DcMotor.class, Constants.ELEVATOR_MOTOR_ID),
                        hardwareMap.get(DcMotor.class, Constants.ARM_MOTOR_ID),
                        new ServoClamp(
                                hardwareMap.get(Servo.class, Constants.CLAMP_SERVO_ID),
                                PLASTIC_CLAMP_OPEN_POSITION,
                                PLASTIC_CLAMP_CLOSED_POSITION,
                                PLASTIC_CLAMP_ID),
                        new ServoClamp(
                                hardwareMap.get(Servo.class, Constants.BASKET_SERVO_ID),
                                BASKET_OPEN_POSITION,
                                BASKET_CLOSED_POSITION,
                                BASKET_ID),
                        hardwareMap.get(Servo.class, Constants.ROTATION_SERVO_ID));
        arm.tryResetMotors();
        arm.setTeam(this.team);
        arm.setColorSensor(
                hardwareMap.get(ColorSensor.class, Constants.CLAMP_COLOR_SENSOR_ID),
                this.colorSensorAutomation);

        if (replay) {
            replaySaver = new Replayer.Logger(runtime, new RobotModule[] {move, arm});
        }

        hardwareMap.get(Servo.class, "fixed_servo").setPosition(0.8);
    }

    @Override
    public void init_loop() {
        runtime.reset();
    }

    @Override
    public void start() {
        runtime.reset();
        arm.initSequence();
        arm.toggleRotation();

        arm.apply();
    }

    @Override
    public void loop() {
        /* --- DEBUG TOOLS --- */
        //        if (gamepad.press(GamepadController.Button.B) && DEBUG) {
        //            feature_disabled = !feature_disabled;
        //        }

        /* --- MOVEMENT --- */
        move.reset();

        /*
        if (gamepad.press(GamepadController.Button.LEFT_STICK)) {
            if (move.getMovementMode() == Movement.MovementMode.ROBOT_CENTRIC) {
                move.setMovementMode(Movement.MovementMode.FIELD_CENTRIC);
            } else {
                move.setMovementMode(Movement.MovementMode.ROBOT_CENTRIC);
            }
        }
        */

        // Translation : unpressed (fast) and pressed (slow)
        move.joystickTranslate(gamepad1, gamepad.leftStickPressed());

        // Rotation : bumpers (fast) and triggers (slow)
        move.bumperTurn(gamepad1);

        /* --- ARM --- */
        arm.joystickSelect(gamepad1);
        if (gamepad.press(GamepadController.Button.RIGHT_STICK)) {
            arm.handleGamepadAction();
        }

        if (gamepad.press(GamepadController.Button.B)) {
            arm.transfer();
        }

        if (gamepad.press(GamepadController.Button.Y)) {
            arm.goToElevatorPos(Arm.ElevatorPosition.HIGH);
        } else if (gamepad.press(GamepadController.Button.A)) {
            arm.goToElevatorPos(Arm.ElevatorPosition.DEFAULT);
        }

        if (gamepad.press(GamepadController.Button.X)) {
            if (arm.isAtPosition(Arm.ArmPosition.LOW) && arm.isClampAtState(Clamp.State.OPEN)) {
                arm.goToArmPosition(Arm.ArmPosition.LOW_INTAKE);
            } else {
                arm.toggleClamps();
            }
        }

        if (gamepad.press(GamepadController.Button.OPTIONS)) {
            arm.toggleRotation();
        }

        if (gamepad.press(GamepadController.Button.HOME)) {
            arm.goToArmPosition(Arm.ArmPosition.DEFAULT);
        }

        if (move.isMoving()
                && arm.isAtPosition(Arm.ArmPosition.LOW_INTAKE)
                && arm.isClampAtState(Clamp.State.CLOSED)) {
            arm.goToArmPosition(Arm.ArmPosition.LOW);
        }

        /* --- OTHERS --- */
        if (gamepad.press(GamepadController.Button.SHARE)) {
            arm.hangSequence();
        }

        /* --- OPMODE TELEMETRY --- */
        if (DEBUG) {
            globalTelemetry.addLine("--- MANUAL MODE ---");
            globalTelemetry.addData("Features disabled", feature_disabled);
        }

        /* --- APPLY --- */
        move.apply();
        arm.apply();

        globalTelemetry.update();

        /* --- REPLAY --- */
        if (replay) {
            replaySaver.logCurrentState();
        }
    }

    @Override
    public void stop() {
        if (replay) {
            replaySaver.saveAndExit(Constants.REPLAY_FILE_DEST);
        }
    }
}
