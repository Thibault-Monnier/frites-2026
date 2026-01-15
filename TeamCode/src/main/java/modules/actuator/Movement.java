package modules.actuator;

import android.util.Pair;

import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import logic.RobotPosition;
import logic.Team;

import math.Pose2D;
import math.Position2D;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;
import java.util.Objects;

@Config
public class Movement implements RobotActuatorModule {
    /* --- CONSTANTS --- */
    private static final DcMotor.ZeroPowerBehavior DEFAULT_BEHAVIOR =
            DcMotor.ZeroPowerBehavior.BRAKE;
    public static double FRONT_LEFT_COEFF = 1;
    public static double FRONT_RIGHT_COEFF = 1;
    public static double BACK_LEFT_COEFF = 1;
    public static double BACK_RIGHT_COEFF = 1;

    /* --- CLASS FIELDS --- */

    // CONSTRUCTOR FIELDS
    private final Telemetry globalTelemetry;
    private final DcMotor frontLeftDrive;
    private final DcMotor frontRightDrive;
    private final DcMotor backLeftDrive;
    private final DcMotor backRightDrive;

    // NUMERIC FIELDS
    public double frontLeftPower = 0;
    public double frontRightPower = 0;
    public double backLeftPower = 0;
    public double backRightPower = 0;

    // OTHER FIELDS
    private final MovementMode movementMode;

    private boolean isSuperSlow = false;

    private final double speedMultiplier = 1.0;
    private final double slowSpeedMultiplier = 0.5;
    private final double superSlowSpeedMultiplier = 0.2;

    public Movement(
            Telemetry globalTelemetry,
            DcMotor FL,
            DcMotor FR,
            DcMotor BL,
            DcMotor BR,
            MovementMode movementMode,
            IMU globalImu) {
        this.globalTelemetry = globalTelemetry;
        this.frontLeftDrive = FL;
        this.frontRightDrive = FR;
        this.backLeftDrive = BL;
        this.backRightDrive = BR;

        frontLeftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        this.movementMode = movementMode;

        IMU.Parameters parameters =
                new IMU.Parameters(
                        new RevHubOrientationOnRobot(
                                RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD,
                                RevHubOrientationOnRobot.UsbFacingDirection.LEFT));
        globalImu.initialize(parameters);
    }

    /// Rotates the robot using input from the *right* joystick of the gamepad.
    public void joystickRotate(Gamepad gamepad, boolean slow) {
        double turn = gamepad.right_stick_x * speedMultiplier(slow);

        turn = smooth(turn);

        move(0, 0, turn);
    }

    /// Translates the robot using input from the *left* joystick of the gamepad.
    public void joystickTranslate(
            Gamepad gamepad, boolean slow, @Nullable RobotPosition robotPosition, Team team) {
        Pair<Double, Double> speed = getTranslationSpeed(gamepad, slow);

        if (movementMode == MovementMode.FIELD_CENTRIC) {
            if (robotPosition == null) {
                throw new IllegalArgumentException(
                        "Robot position cannot be null in field-centric mode");
            }
            moveFieldCentric(speed.first, speed.second, 0, robotPosition, team);
        } else {
            move(speed.first, speed.second, 0);
        }
    }

    /// Moves while turning towards a target position.
    public void lockedJoystickTranslate(
            Gamepad gamepad,
            boolean slow,
            RobotPosition robotPosition,
            Team team,
            Position2D targetPos) {
        Pair<Double, Double> speed = getTranslationSpeed(gamepad, slow);

        Pose2D robotPose = robotPosition.getPose();

        double dx = targetPos.getX(DistanceUnit.INCH) - robotPose.getX(DistanceUnit.INCH);
        double dy = targetPos.getY(DistanceUnit.INCH) - robotPose.getY(DistanceUnit.INCH);
        double targetDirection = Math.atan2(dy, dx);
        double angleError =
                AngleUnit.normalizeRadians(
                        targetDirection - robotPose.getHeading(AngleUnit.RADIANS));

        double kP = 1.3;
        double turnSpeed = -angleError * kP;
        globalTelemetry.addData("Angle Error (deg)", Math.toDegrees(angleError));
        globalTelemetry.addData("Turn Speed", turnSpeed);
        turnSpeed = Math.clamp(turnSpeed, -1, 1);

        if (movementMode == MovementMode.FIELD_CENTRIC) {
            moveFieldCentric(speed.first, speed.second, turnSpeed, robotPosition, team);
        } else {
            move(speed.first, speed.second, turnSpeed);
        }
    }

    /// Computes translation speed forward and sideways. Returns the pair (forward, strafe).
    private Pair<Double, Double> getTranslationSpeed(Gamepad gamepad, boolean slow) {
        double forward = gamepad.left_stick_y * speedMultiplier(slow);
        forward = smooth(forward);

        double strafe = gamepad.left_stick_x * speedMultiplier(slow);
        strafe = smooth(strafe);

        return new Pair<>(forward, strafe);
    }

    /// Get movement speed multiplier
    private double speedMultiplier(boolean slow) {
        if (isSuperSlow) {
            return superSlowSpeedMultiplier;
        }
        return slow ? slowSpeedMultiplier : speedMultiplier;
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

    private void moveFieldCentric(
            double front, double sideways, double turn, RobotPosition robotPosition, Team team) {
        double robotAngle = robotPosition.getPose().getHeading(AngleUnit.RADIANS);

        if (team.isBlue()) robotAngle -= Math.PI / 2;
        if (team.isRed()) robotAngle += Math.PI / 2;

        double newFront = -front * Math.cos(robotAngle) - sideways * Math.sin(robotAngle);
        double newSideways = front * Math.sin(robotAngle) - sideways * Math.cos(robotAngle);

        move(newFront, newSideways, turn);
    }

    private void move(double front, double sideways, double turn) {
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
    }

    /// Toggles super slow mode
    public void toggleSuperSlow() {
        isSuperSlow = !isSuperSlow;
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
        globalTelemetry.addData("Front Left", frontLeftDrive.getPower());
        globalTelemetry.addData("Front Right", frontRightDrive.getPower());
        globalTelemetry.addData("Back Left", backLeftDrive.getPower());
        globalTelemetry.addData("Back Right", backRightDrive.getPower());
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
