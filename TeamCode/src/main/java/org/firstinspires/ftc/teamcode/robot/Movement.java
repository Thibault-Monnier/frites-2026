package org.firstinspires.ftc.teamcode.robot;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.models.RobotModule;

import java.util.HashMap;
import java.util.Objects;

@Config
public class Movement implements RobotModule {
    /* --- CONSTANTS --- */
    private static final DcMotor.ZeroPowerBehavior DEFAULT_BEHAVIOR =
            DcMotor.ZeroPowerBehavior.BRAKE;
    public static boolean DEBUG = true;
    public static double FRONT_LEFT_COEFF = 1;
    public static double FRONT_RIGHT_COEFF = 1;
    public static double BACK_LEFT_COEFF = 1;
    public static double BACK_RIGHT_COEFF = 1;

    public static double BUMPER_TURN_VALUE = 0.5; // 0.3;
    public static double TRIGGER_TURN_VALUE = 0.8;

    /* --- CLASS FIELDS --- */

    // CONSTRUCTOR FIELDS
    private final Telemetry globalTelemetry;
    private final DcMotor frontLeftDrive;
    private final DcMotor frontRightDrive;
    private final DcMotor backLeftDrive;
    private final DcMotor backRightDrive;
    private final IMU globalImu;

    // NUMERIC FIELDS
    public double frontLeftPower = 0;
    public double frontRightPower = 0;
    public double backLeftPower = 0;
    public double backRightPower = 0;

    private double turn = 0f;

    // OTHER FIELDS
    private MovementMode movementMode;

    public Movement(
            Telemetry globalTelemetry,
            DcMotor FL,
            DcMotor FR,
            DcMotor BL,
            DcMotor BR,
            IMU globalImu) {
        this.globalTelemetry = globalTelemetry;
        this.frontLeftDrive = FL;
        this.frontRightDrive = FR;
        this.backLeftDrive = BL;
        this.backRightDrive = BR;
        this.globalImu = globalImu;

        frontLeftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        IMU.Parameters parameters =
                new IMU.Parameters(
                        new RevHubOrientationOnRobot(
                                RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD,
                                RevHubOrientationOnRobot.UsbFacingDirection.LEFT));
        globalImu.initialize(parameters);
    }

    public MovementMode getMovementMode() {
        return movementMode;
    }

    public void setMovementMode(MovementMode mode) {
        movementMode = mode;
    }

    public void bumperTurn(Gamepad gamepad) {
        if (gamepad.left_bumper || gamepad.right_bumper) {
            if (gamepad.left_bumper) turn -= BUMPER_TURN_VALUE;
            if (gamepad.right_bumper) turn += BUMPER_TURN_VALUE;
        } else {
            turn += gamepad.right_trigger * TRIGGER_TURN_VALUE;
            turn -= gamepad.left_trigger * TRIGGER_TURN_VALUE;
        }
        move(0, 0, turn);
    }

    public void joystickTranslate(Gamepad gamepad, boolean slow) {
        double speedMultiplier = slow ? 0.5 : 1;

        double sideways = gamepad.left_stick_x * speedMultiplier;
        double front = gamepad.left_stick_y * speedMultiplier;

        // Gradual speed increase
        sideways = smooth(sideways);
        front = smooth(front);

        move(front, sideways, 0);
    }

    private double smooth(double input) {
        if (Math.abs(input) < 0.1) {
            return 0;
        } else if (Math.abs(input) >= 0.1 || Math.abs(input) < 0.7) {
            return 0.83 * input + 0.02;
        } else if (Math.abs(input) >= 0.7 || Math.abs(input) < 0.9) {
            return 2 * input - 0.8;
        } else if (Math.abs(input) >= 0.9) {
            return 1;
        }
        return 0;
    }

    public void move(double front, double sideways, double turn) {
        if (movementMode == MovementMode.FIELD_CENTRIC) {
            YawPitchRollAngles robotOrientation = globalImu.getRobotYawPitchRollAngles();

            //             [ cos(θ)  -sin(θ) ]   [   front  ]
            // N = R * O = [                 ] * [          ]
            //             [ sin(θ)   cos(θ) ]   [ sideways ]
            //     [ cos(θ)*front - sin(θ)*sideways ]
            //   = [                                ]
            //     [ sin(θ)*front + cos(θ)*sideways ]

            final double theta = -robotOrientation.getYaw(AngleUnit.RADIANS);
            front = front * Math.cos(theta) - sideways * Math.sin(theta);
            sideways = front * Math.sin(theta) + sideways * Math.cos(theta);
        }
        double denominator = Math.max(Math.abs(front) + Math.abs(sideways) + Math.abs(turn), 1);
        frontLeftPower += (front - sideways - turn) / denominator;
        backLeftPower += (front + sideways - turn) / denominator;
        frontRightPower += (front + sideways + turn) / denominator;
        backRightPower += (front - sideways + turn) / denominator;

        frontLeftPower *= FRONT_LEFT_COEFF;
        frontRightPower *= FRONT_RIGHT_COEFF;
        backLeftPower *= BACK_LEFT_COEFF;
        backRightPower *= BACK_RIGHT_COEFF;
    }

    public void reset() {
        frontLeftPower = 0;
        frontRightPower = 0;
        backLeftPower = 0;
        backRightPower = 0;
        turn = 0;
    }

    public boolean isMoving() {
        return Math.abs(frontLeftPower) > 0.1
                || Math.abs(frontRightPower) > 0.1
                || Math.abs(backLeftPower) > 0.1
                || Math.abs(backRightPower) > 0.1;
    }

    public void apply() {
        frontLeftDrive.setZeroPowerBehavior(DEFAULT_BEHAVIOR);
        frontRightDrive.setZeroPowerBehavior(DEFAULT_BEHAVIOR);
        backLeftDrive.setZeroPowerBehavior(DEFAULT_BEHAVIOR);
        backRightDrive.setZeroPowerBehavior(DEFAULT_BEHAVIOR);

        frontLeftDrive.setPower(Range.clip(frontLeftPower, -1.0, 1.0));
        frontRightDrive.setPower(Range.clip(frontRightPower, -1.0, 1.0));
        backLeftDrive.setPower(Range.clip(backLeftPower, -1.0, 1.0));
        backRightDrive.setPower(Range.clip(backRightPower, -1.0, 1.0));

        globalTelemetry.addLine("--- MOVEMENT ---");
        globalTelemetry.addData("Mode", movementMode);
        if (DEBUG) {
            globalTelemetry.addData("Front Left", frontLeftDrive.getPower());
            globalTelemetry.addData("Front Right", frontRightDrive.getPower());
            globalTelemetry.addData("Back Left", backLeftDrive.getPower());
            globalTelemetry.addData("Back Right", backRightDrive.getPower());
            globalTelemetry.addData("Yaw", globalImu.getRobotYawPitchRollAngles().getYaw());
            globalTelemetry.addData("Pitch", globalImu.getRobotYawPitchRollAngles().getPitch());
            globalTelemetry.addData("Roll", globalImu.getRobotYawPitchRollAngles().getRoll());
        }
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        return new HashMap<String, Object>() {
            {
                put("frontLeftPower", frontLeftPower);
                put("frontRightPower", frontRightPower);
                put("backLeftPower", backLeftPower);
                put("backRightPower", backRightPower);
            }
        };
    }

    @Override
    public void setState(HashMap<String, String> state) {
        frontLeftPower = Double.parseDouble(Objects.requireNonNull(state.get("frontLeftPower")));
        frontRightPower = Double.parseDouble(Objects.requireNonNull(state.get("frontRightPower")));
        backLeftPower = Double.parseDouble(Objects.requireNonNull(state.get("backLeftPower")));
        backRightPower = Double.parseDouble(Objects.requireNonNull(state.get("backRightPower")));
    }

    public enum MovementMode {
        ROBOT_CENTRIC,
        FIELD_CENTRIC
    }
}
