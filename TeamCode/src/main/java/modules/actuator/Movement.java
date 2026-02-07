package modules.actuator;

import static config.MovementConfig.BACK_LEFT_COEFF;
import static config.MovementConfig.BACK_RIGHT_COEFF;
import static config.MovementConfig.FRONT_LEFT_COEFF;
import static config.MovementConfig.FRONT_RIGHT_COEFF;
import static config.MovementConfig.NOT_TURNING_THRESHOLD;
import static config.MovementConfig.SLOW_SPEED_MULTIPLIER;
import static config.MovementConfig.SPEED_MULTIPLIER;
import static config.MovementConfig.SUPER_SLOW_SPEED_MULTIPLIER;
import static config.MovementConfig.TURN_PIDF_COEFFICIENTS;
import static config.MovementConfig.TURN_TOLERANCE;

import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;

import logic.Team;
import logic.pidf.PIDFController;
import logic.position.RobotPosition;

import math.Angle;
import math.Pose2D;
import math.Position2D;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;
import java.util.Objects;

@Config
public class Movement implements RobotActuatorModule {
    private final Telemetry globalTelemetry;

    private final DcMotor frontLeftDrive;
    private final DcMotor frontRightDrive;
    private final DcMotor backLeftDrive;
    private final DcMotor backRightDrive;

    public double frontLeftPower = 0;
    public double frontRightPower = 0;
    public double backLeftPower = 0;
    public double backRightPower = 0;

    private final PIDFController turnController;

    private final MovementMode movementMode;

    private boolean isSuperSlow = false;

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

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        this.turnController = new PIDFController(globalTelemetry, TURN_PIDF_COEFFICIENTS);

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
        double turn = -gamepad.right_stick_x * speedMultiplier(slow);

        turn = smooth(turn);
        turn(turn);
    }

    /// Translates the robot using input from the *left* joystick of the gamepad.
    public void joystickTranslate(
            Gamepad gamepad, boolean slow, @Nullable RobotPosition robotPosition, Team team) {
        Translation velocity = getTranslationVelocity(gamepad, slow);

        if (movementMode == MovementMode.FIELD_CENTRIC) {
            if (robotPosition == null) {
                throw new IllegalArgumentException(
                        "Robot position cannot be null in field-centric mode");
            }
            translateFieldCentric(velocity, robotPosition, team);
        } else {
            translate(velocity);
        }
    }

    /// Moves while turning towards a target position.
    public void lockedJoystickMove(
            Gamepad gamepad,
            boolean slow,
            RobotPosition robotPosition,
            Team team,
            Position2D targetPos) {
        Translation velocity = getTranslationVelocity(gamepad, slow);

        turnTowards(robotPosition, targetPos);

        if (movementMode == MovementMode.FIELD_CENTRIC) {
            translateFieldCentric(velocity, robotPosition, team);
        } else {
            translate(velocity);
        }
    }

    public boolean turnTowards(RobotPosition robotPosition, Position2D targetPos) {
        Pose2D robotPose = robotPosition.getPose();

        double dx = targetPos.getX(DistanceUnit.INCH) - robotPose.getX(DistanceUnit.INCH);
        double dy = targetPos.getY(DistanceUnit.INCH) - robotPose.getY(DistanceUnit.INCH);
        Angle targetDirection = Angle.fromRadians(Math.atan2(dy, dx));

        return turnTowardsHeading(robotPosition, targetDirection);
    }

    public boolean turnTowardsHeading(RobotPosition robotPosition, Angle targetHeading) {
        Pose2D robotPose = robotPosition.getPose();

        Angle angleError = targetHeading.subtract(robotPose.getHeading());
        turnController.setError(angleError.toRadians());

        double turnSpeed = turnController.get();
        turn(turnSpeed);

        boolean isFinished =
                turnController.isStableAtTarget(
                        TURN_TOLERANCE.toRadians(), NOT_TURNING_THRESHOLD.toRadians());
        globalTelemetry.addData("Turn Speed", turnSpeed);
        globalTelemetry.addData("Error change", turnController.getErrorChange());
        return !isFinished;
    }

    /// Computes translation speed forward and sideways. Returns the pair (forward, strafe).
    private Translation getTranslationVelocity(Gamepad gamepad, boolean slow) {
        double forward = gamepad.left_stick_y * speedMultiplier(slow);
        forward = smooth(forward);

        double strafe = gamepad.left_stick_x * speedMultiplier(slow);
        strafe = smooth(strafe);

        return new Translation(forward, strafe);
    }

    /// Get movement speed multiplier
    private double speedMultiplier(boolean slow) {
        if (isSuperSlow) {
            return SUPER_SLOW_SPEED_MULTIPLIER;
        }
        return slow ? SLOW_SPEED_MULTIPLIER : SPEED_MULTIPLIER;
    }

    private double smooth(double input) {
        if (Math.abs(input) < 0.1) {
            return 0;
        } else if (Math.abs(input) >= 0.1 && Math.abs(input) < 0.7) {
            return 0.83 * input + 0.02;
        } else if (Math.abs(input) >= 0.7 && Math.abs(input) < 0.9) {
            return 2 * input - 0.8;
        } else if (Math.abs(input) >= 0.9) {
            return Math.signum(input); // full speed
        }
        return 0;
    }

    private void translateFieldCentric(
            Translation translation, RobotPosition robotPosition, Team team) {
        double robotAngle = robotPosition.getPose().getHeading(AngleUnit.RADIANS);

        if (team.isBlue()) robotAngle -= Math.PI / 2;
        if (team.isRed()) robotAngle += Math.PI / 2;

        double forward = translation.forward;
        double strafe = translation.strafe;

        translation.forward = -forward * Math.cos(robotAngle) - strafe * Math.sin(robotAngle);
        translation.strafe = forward * Math.sin(robotAngle) - strafe * Math.cos(robotAngle);

        translate(translation);
    }

    private void turn(double turnSpeed) {
        move(new Translation(), turnSpeed);
    }

    private void translate(Translation translation) {
        move(translation, 0);
    }

    private void move(Translation translation, double turnSpeed) {
        double forward = translation.forward;
        double strafe = translation.strafe;

        double denominator =
                Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(turnSpeed), 1);

        frontLeftPower += (forward - strafe + turnSpeed) / denominator;
        frontRightPower += (forward + strafe - turnSpeed) / denominator;
        backLeftPower += (forward + strafe + turnSpeed) / denominator;
        backRightPower += (forward - strafe - turnSpeed) / denominator;

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
        frontLeftDrive.setPower(Math.clamp(frontLeftPower, -1.0, 1.0));
        frontRightDrive.setPower(Math.clamp(frontRightPower, -1.0, 1.0));
        backLeftDrive.setPower(Math.clamp(backLeftPower, -1.0, 1.0));
        backRightDrive.setPower(Math.clamp(backRightPower, -1.0, 1.0));

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

    private static class Translation {
        public double forward;
        public double strafe;

        public Translation(double forward, double strafe) {
            this.forward = forward;
            this.strafe = strafe;
        }

        public Translation() {
            this(0, 0);
        }
    }
}
