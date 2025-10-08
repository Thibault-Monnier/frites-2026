package org.firstinspires.ftc.teamcode.robot;

import static org.firstinspires.ftc.teamcode.robot.Constants.ColorRange.YELLOW;

import android.graphics.Color;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.models.Clamp;
import org.firstinspires.ftc.teamcode.models.RobotModule;

import java.util.HashMap;

@Config
public class Arm implements RobotModule {
    /* --- CONSTANTS --- */
    // ARM
    public static double ARM_ACTIVE_POWER = 0.9;
    public static double ARM_REST_POWER = 0.5;
    public static int ARM_DEFAULT_POS = 0;
    public static int ARM_SIDE_INTAKE_POS = 500;
    public static int ARM_HANG_POS = 600;
    public static int ARM_TRANSFER_POS = 1870;
    public static int ARM_HIGH_POS = 2920; //
    public static int ARM_INTAKE_POS = 4700; //4800;
    public static int ARM_LOW_POS = 4300;
    public static int ARM_LOW_INTAKE_EXTRA = 100;
    // REVERSE KINEMATICS
    public static double ARM_INIT_ANGLE = -37;
    public static double ARM_MOTOR_OUTER_RATIO = (20. / 100.);
    public static double ARM_MOTOR_TICKS_PER_ROTATION = 1425;
    public static double ARM_MOTOR_DEGREE_PER_TICKS = 360 / (ARM_MOTOR_TICKS_PER_ROTATION / ARM_MOTOR_OUTER_RATIO);
    // ELEVATOR
    public static double ELEVATOR_ACTIVE_POWER = 0.7;
    public static double ELEVATOR_REST_POWER = 0.5;
    public static int ELEVATOR_TRANSFER_POS = 650;
    public static int ELEVATOR_LAISSER_PASSER_POS = 1900;
    public static int ELEVATOR_HIGH_POS = 2800; // 3500; // 3700;
    public static int ELEVATOR_DEFAULT_POS = 0;
    public static int ENCODER_POSITION_ERROR = 30;
    public static float GAMEPAD_JOYSTICK_MIN = 0.7f;
    public static int RUMBLE_ERROR_DURATION = 200;
    public static double CLAMP_ROTATION_RANGE = 270;
    private final boolean DEBUG;
    // CONSTRUCTOR
    private final DcMotor armMotor;
    private final DcMotor elevatorMotor;
    private final Telemetry telemetry;
    private final Clamp clamp;
    private final Clamp basket;
    private final Servo clampRotator;
    private static boolean motorsReset = false;
    private Constants.Team team = Constants.Team.ANY_OR_UNKNOWN;
    private ColorSensor colorSensor;
    private ColorSensorMode colorSensorMode = ColorSensorMode.NO_DETECTION;
    // ELEVATOR
    private ElevatorPosition currentElevatorPosition = ElevatorPosition.DEFAULT;
    private ElevatorPosition targetElevatorPosition = ElevatorPosition.DEFAULT;
    private ElevatorPosition lastElevatorPosition = ElevatorPosition.DEFAULT;
    private boolean isElevatorMoving;
    // ARM
    private ArmPosition currentArmPosition = ArmPosition.DEFAULT;
    private ArmPosition targetArmPosition = ArmPosition.DEFAULT;
    private ArmPosition lastArmPosition = ArmPosition.DEFAULT;
    private ArmPosition gamepadChoice = ArmPosition.DEFAULT;
    private boolean isArmMoving;
    // SPECIAL OPERATIONS
    private boolean initSequence;
    private boolean hangSequence;
    // CLAMP ROTATION
    private boolean isClampAtMax;
    private double targetRotation;

    {
        DEBUG = true;
    }

    public Arm(Telemetry globalTelemetry, DcMotor elevatorMotor, DcMotor armMotor, Clamp clamp, Clamp basket, Servo clampRotator) {
        this.armMotor = armMotor;
        this.elevatorMotor = elevatorMotor;
        this.telemetry = globalTelemetry;
        this.clamp = clamp;
        this.basket = basket;
        this.clampRotator = clampRotator;


//        this.armMotor.resetDeviceConfigurationForOpMode();
//        this.armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

//        this.elevatorMotor.resetDeviceConfigurationForOpMode();
//        this.elevatorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.elevatorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.elevatorMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        this.isClampAtMax = false;

        initSequence = false;
        hangSequence = false;
    }

    private static boolean isAtPosition(int pos, int currentPos) {
        return Math.abs(currentPos - pos) <= ENCODER_POSITION_ERROR;
    }

    /**
     * Resets the motors if they haven't been reset yet.
     * <p>
     * This allows for motors to be reset only once at the start of the first OpMode, and not
     * when switching between OpModes during a match.
     */
    public void tryResetMotors() {
        if (motorsReset) return;
        motorsReset = true;

        this.armMotor.resetDeviceConfigurationForOpMode();
        this.armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.elevatorMotor.resetDeviceConfigurationForOpMode();
        this.elevatorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setTeam(Constants.Team team) {
        this.team = team;
    }

    public void setColorSensor(ColorSensor colorSensor, ColorSensorMode mode) {
        this.colorSensor = colorSensor;
        this.colorSensorMode = mode;
    }

    public int getArmEncoderPosition() {
        return armMotor.getCurrentPosition();
    }

    public int getElevatorEncoderPosition() {
        return elevatorMotor.getCurrentPosition();
    }

    public void goToElevatorPos(ElevatorPosition pos) {
        targetElevatorPosition = pos;
    }

    public void initSequence() {
        goToArmPosition(ArmPosition.HIGH);

        clamp.close();

        initSequence = true;
    }

    public void hangSequence() {
        if (currentElevatorPosition == ElevatorPosition.DEFAULT) {
            goToArmPosition(ArmPosition.HANG);

            hangSequence = true;
        }
    }

    public void joystickSelect(Gamepad gamepad) {
        if (gamepad.right_stick_x > GAMEPAD_JOYSTICK_MIN) {
            gamepadChoice = ArmPosition.SIDE_INTAKE;
        } else if (gamepad.right_stick_x < -GAMEPAD_JOYSTICK_MIN) {
            gamepadChoice = ArmPosition.LOW;
        } else if (gamepad.right_stick_y > GAMEPAD_JOYSTICK_MIN) {
            gamepadChoice = ArmPosition.LOW_INTAKE;
        } else if (gamepad.right_stick_y < -GAMEPAD_JOYSTICK_MIN) {
            gamepadChoice = ArmPosition.HIGH;
        }

        if (gamepadChoice == ArmPosition.DEFAULT
                && currentElevatorPosition.encoderPos <= ElevatorPosition.LAISSER_PASSER.encoderPos) {
            gamepadChoice = currentArmPosition;
            gamepad.rumble(RUMBLE_ERROR_DURATION);
        }
    }

    public void transfer() {
        if (currentArmPosition != ArmPosition.DEFAULT && currentElevatorPosition == ElevatorPosition.DEFAULT) {
            goToArmPosition(ArmPosition.TRANSFER);
            goToElevatorPos(ElevatorPosition.TRANSFER);
            isClampAtMax = true;
        } else {
            if (clamp.getCurrentState().get(clamp.getStateKey()) == Clamp.State.CLOSED) {
                goToElevatorPos(ElevatorPosition.DEFAULT);
            } else {
                goToElevatorPos(ElevatorPosition.HIGH);
                isClampAtMax = true;
            }
            goToArmPosition(ArmPosition.LOW_INTAKE);
        }
    }

    public void handleGamepadAction() {
        if (!(initSequence || hangSequence)) {
            goToArmPosition(gamepadChoice);
        }
    }

    public void goToArmPosition(ArmPosition position) {
        targetArmPosition = position;
    }

    public boolean isMoving() {
        return isArmMoving || isElevatorMoving;
    }

    public void toggleClamps() {
        if (currentElevatorPosition == ElevatorPosition.HIGH) {
            if (basket.getCurrentState().get(basket.getStateKey()) == Clamp.State.OPEN) {
                basket.close();
            } else {
                basket.open();
            }
        } else {
            if (clamp.getCurrentState().get(clamp.getStateKey()) == Clamp.State.OPEN) {
                clamp.close();
            } else {
                clamp.open();
            }
        }
    }

    public void toggleRotation() {
        isClampAtMax = !isClampAtMax;
//        clamp.close();
    }

    private void setRotation(double rotation) {
        targetRotation = rotation;
    }

    private double clampReverseKinematics(double targetAngle) {
        // the angle 0 is directed upwards
        // the position 0 of the servo translates to the clamp being in the continuity of the arm
        // positive angle are towards the front, negative towards the back
        // (0 is actually slightly offset to the front)

        int currentArmPos = getArmEncoderPosition();
        double armAngle = currentArmPos * ARM_MOTOR_DEGREE_PER_TICKS + ARM_INIT_ANGLE;

        double correction = Range.clip(
                armAngle - Math.signum(armAngle - 90) * targetAngle - 90,
                -CLAMP_ROTATION_RANGE,
                CLAMP_ROTATION_RANGE
        );

        return (correction / CLAMP_ROTATION_RANGE) + .5;
    }

    public boolean isAtPosition(ArmPosition position) {
        return currentArmPosition == position;
    }

    public Clamp getClamp() {
        return clamp;
    }

    public Clamp getBasket() {
        return basket;
    }

    public boolean isClampAtState(Clamp.State state) {
        return clamp.getCurrentState().get(clamp.getStateKey()) == state;
    }

    public void apply() {
        /* --- CLAMP ROTATION --- */
        if (isClampAtMax && currentArmPosition != ArmPosition.DEFAULT) {
            if (currentArmPosition != ArmPosition.UNKNOWN) {
                targetRotation = currentArmPosition.maxRotation;
            }
        } else {
            targetRotation = 0;
        }


        /* --- GENERAL INFOS --- */
        int realArmPosition = getArmEncoderPosition();
        if (targetArmPosition == ArmPosition.LOW_INTAKE && clamp.getCurrentState().get(clamp.getStateKey()) == Clamp.State.CLOSED) {
            realArmPosition -= ARM_LOW_INTAKE_EXTRA;

        }
        currentArmPosition = ArmPosition.getWithEncoderPos(realArmPosition);

        int realElevatorPosition = getElevatorEncoderPosition();
        currentElevatorPosition = ElevatorPosition.getWithEncoderPos(realElevatorPosition);

        /* --- SPECIAL SEQUENCES --- */
        if (initSequence) {
            if (currentArmPosition == ArmPosition.HIGH) {
                initSequence = false;
            }
        }

        if (hangSequence) {
            if (currentArmPosition == ArmPosition.HANG) {
                hangSequence = false;
            }
        }


        /* --- ARM --- */
        if (currentArmPosition != ArmPosition.UNKNOWN) {
            lastArmPosition = currentArmPosition;
        }

        if (currentArmPosition != targetArmPosition) {
            isArmMoving = true;

            armMotor.setTargetPosition(
                    targetArmPosition.encoderPos
                            + (
                            targetArmPosition == ArmPosition.LOW_INTAKE &&
                                    clamp.getCurrentState().get(clamp.getStateKey()) == Clamp.State.CLOSED
                                    ? ARM_LOW_INTAKE_EXTRA : 0
                    )
            );
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armMotor.setPower(ARM_ACTIVE_POWER);
        } else {
            isArmMoving = false;
            armMotor.setPower(ARM_REST_POWER);
        }


        /* --- ELEVATOR --- */
        if (currentElevatorPosition != ElevatorPosition.UNKNOWN) {
            lastElevatorPosition = currentElevatorPosition;
        }

        if (currentElevatorPosition != targetElevatorPosition) {
            isElevatorMoving = true;

            elevatorMotor.setTargetPosition(targetElevatorPosition.encoderPos);
            elevatorMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            elevatorMotor.setPower(ELEVATOR_ACTIVE_POWER);
        } else {
            isElevatorMoving = false;
            if (targetElevatorPosition == ElevatorPosition.DEFAULT) {
                elevatorMotor.setPower(0);
            } else {
                elevatorMotor.setPower(ELEVATOR_REST_POWER);
            }
        }


        /* --- BASKET --- */
        if ((lastArmPosition.encoderPos < ArmPosition.TRANSFER.encoderPos && ArmPosition.TRANSFER.encoderPos < targetArmPosition.encoderPos)
                || (targetArmPosition.encoderPos < ArmPosition.TRANSFER.encoderPos && ArmPosition.TRANSFER.encoderPos <= lastArmPosition.encoderPos)) {
            basket.open();
        } else {
            if (currentElevatorPosition != ElevatorPosition.HIGH) {
                basket.close();
            }
        }

        /* --- COLOR SENSOR --- */
        if (colorSensor != null && colorSensorMode != ColorSensorMode.NO_DETECTION) {
            if (currentArmPosition == ArmPosition.LOW_INTAKE && clamp.getCurrentState().get(clamp.getStateKey()) == Clamp.State.OPEN) {
                int r = colorSensor.red();
                int g = colorSensor.green();
                int b = colorSensor.blue();

                float[] hsv = new float[3];
                Color.RGBToHSV(r, g, b, hsv);

                int hue = Math.round(hsv[0]);

                if (colorSensorMode.defaultColor) {
                    if (YELLOW.valueInRange(hue)) {
                        clamp.close();
                    }
                }

                if (colorSensorMode.teamColor) {
                    Constants.ColorRange range = Constants.ColorRange.UNKNOW;
                    switch (team) {
                        case RED:
                            range = Constants.ColorRange.RED;
                            break;

                        case BLUE:
                            range = Constants.ColorRange.BLUE;
                            break;

                        case ANY_OR_UNKNOWN:
                            break;
                    }

                    if (range != Constants.ColorRange.UNKNOW) {
                        if (range.valueInRange(hue)) {
                            clamp.close();
                        }
                    }
                }
            }
        }


        /* --- APPLY TO SERVOS --- */
        clampRotator.setPosition(clampReverseKinematics(targetRotation));

        clamp.apply();
        basket.apply();


        /* --- TELEMETRY --- */
        telemetry.addLine("--- ARM ---");
        if (DEBUG) {
            telemetry.addData("Real arm position", getArmEncoderPosition());
            telemetry.addData("Real elevator position", getElevatorEncoderPosition());
            telemetry.addData("Elevator state", elevatorMotor.isBusy());
        }
        telemetry.addData("Arm module moving", isMoving());
        telemetry.addData("Clamp state", clamp.getCurrentState().get(clamp.getStateKey()));
        telemetry.addData("Basket state", basket.getCurrentState().get(basket.getStateKey()));
        telemetry.addLine("=> Arm");
        telemetry.addData("Current position", currentArmPosition);
        telemetry.addData("Target position", targetArmPosition);
        telemetry.addData("Motor power", armMotor.getPower());
        telemetry.addLine("=> Elevator");
        telemetry.addData("Current position type", currentElevatorPosition);
        telemetry.addData("Current position value", currentElevatorPosition.encoderPos);
        telemetry.addData("Target position type", targetElevatorPosition);
        telemetry.addData("Target position value", targetElevatorPosition.encoderPos);
        telemetry.addData("Motor power", elevatorMotor.getPower());

        telemetry.addLine();
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();

        state.put("targetArmPosition", targetArmPosition);
        state.put("targetElevatorPosition", targetElevatorPosition);
        state.put("isClampAtMax", isClampAtMax);

        HashMap<String, Object> clampState = clamp.getCurrentState();
        for (String key :
                clampState.keySet()) {
            state.put(key, clampState.get(key));
        }

        HashMap<String, Object> basketState = basket.getCurrentState();
        for (String key :
                basketState.keySet()) {
            state.put(key, basketState.get(key));
        }

        return state;
    }

    @Override
    public void setState(HashMap<String, String> state) {
        targetArmPosition = ArmPosition.valueOf(state.get("targetArmPosition"));
        targetElevatorPosition = ElevatorPosition.valueOf(state.get("targetElevatorPosition"));
        isClampAtMax = Boolean.parseBoolean(state.get("isClampAtMax"));

        clamp.setState(state);
        basket.setState(state);
    }

    public enum ColorSensorMode {
        DEFAULT_ONLY(false, true),
        TEAM_ONLY(true, false),
        TEAM_AND_DEFAULT(true, true),
        NO_DETECTION(false, false);

        final boolean teamColor;
        final boolean defaultColor;

        ColorSensorMode(boolean teamColor, boolean defaultColor) {
            this.teamColor = teamColor;
            this.defaultColor = defaultColor;
        }
    }

    public enum ElevatorPosition {
        DEFAULT(0, ELEVATOR_DEFAULT_POS),
        TRANSFER(1, ELEVATOR_TRANSFER_POS), // 900
        LAISSER_PASSER(2, ELEVATOR_LAISSER_PASSER_POS),
        HIGH(3, ELEVATOR_HIGH_POS),

        UNKNOWN(-1, -1);

        /*
         *                                    _
         *                           (high)  \__/
         *                                  _||
         *                                 ||||
         *             _ _ _              _||
         *            ||||\__/           ||||
         *            ||||||             ||
         * (default) 0||||||            0||
         * */

        final int id;
        final int encoderPos;

        ElevatorPosition(int id, int encoderPos) {
            this.id = id;
            this.encoderPos = encoderPos;
        }

        static public ElevatorPosition getWithId(int id) {
            for (ElevatorPosition position : ElevatorPosition.values()) {
                if (position.id == id) {
                    return position;
                }
            }
            return UNKNOWN;
        }

        static public ElevatorPosition getWithEncoderPos(int pos) {
            for (ElevatorPosition position : ElevatorPosition.values()) {
                if (isAtPosition(position.encoderPos, pos)) {
                    return position;
                }
            }
            return UNKNOWN;
        }
    }

    // T/O/D/O: find encoder positions
    public enum ArmPosition {
        DEFAULT(0, ARM_DEFAULT_POS, 0),
        SIDE_INTAKE(1, ARM_SIDE_INTAKE_POS, 90),
        HANG(2, ARM_HANG_POS, 0),
        TRANSFER(3, ARM_TRANSFER_POS, 90), // 1800
        HIGH(4, ARM_HIGH_POS, 180), //3440 //3850
        LOW(5, ARM_LOW_POS, 180), //4300 //4700
        LOW_INTAKE(6, ARM_INTAKE_POS, 180), //4830

        UNKNOWN(-1, -1, 0);

        /*
         *                    [x] (high)
         *                   //
         *                  //
         *                (°0)=======[x] (low)
         *               //| \\
         *             //  |   \\
         * (default) [x]   |    [x] (intake)
         * */

        final int id;
        final int encoderPos;
        final int maxRotation;

        ArmPosition(int id, int encoderPos, int maxRotation) {
            this.id = id;
            this.encoderPos = encoderPos;
            this.maxRotation = maxRotation;
        }

        static public ArmPosition getWithId(int id) {
            for (ArmPosition position : ArmPosition.values()) {
                if (position.id == id) {
                    return position;
                }
            }
            return UNKNOWN;
        }

        static public ArmPosition getWithEncoderPos(int pos) {
            for (ArmPosition position : ArmPosition.values()) {
                if (isAtPosition(position.encoderPos, pos)) {
                    return position;
                }
            }
            return UNKNOWN;
        }
    }
}
