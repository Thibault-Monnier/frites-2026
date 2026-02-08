package logic;

import static config.MovementConfig.NOT_TRANSLATING_THRESHOLD;
import static config.MovementConfig.NOT_TURNING_THRESHOLD;
import static config.MovementConfig.SLOW_SPEED_MULTIPLIER;
import static config.MovementConfig.SPEED_MULTIPLIER;
import static config.MovementConfig.SUPER_SLOW_SPEED_MULTIPLIER;
import static config.MovementConfig.TRANSLATION_PIDF_COEFFICIENTS;
import static config.MovementConfig.TRANSLATION_TOLERANCE;
import static config.MovementConfig.TURN_PIDF_COEFFICIENTS;
import static config.MovementConfig.TURN_TOLERANCE;

import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import logic.pidf.PIDFController;
import logic.position.RobotPosition;

import math.Angle;
import math.Distance;
import math.Pose2D;
import math.Position2D;
import math.Vector2D;

import modules.actuator.MecanumDrive;
import modules.actuator.RobotActuatorModule;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

@Config
public class Movement implements RobotActuatorModule {
    private final Telemetry globalTelemetry;

    private final MecanumDrive mecanumDrive;

    private final PIDFController turnController;
    private final PIDFController translationController;

    private final MovementMode movementMode;
    private boolean isSuperSlow = false;

    public Movement(
            Telemetry globalTelemetry,
            DcMotor FL,
            DcMotor FR,
            DcMotor BL,
            DcMotor BR,
            MovementMode movementMode) {
        this.globalTelemetry = globalTelemetry;

        this.mecanumDrive = new MecanumDrive(globalTelemetry, FL, FR, BL, BR);
        this.turnController = new PIDFController(globalTelemetry, TURN_PIDF_COEFFICIENTS);
        this.translationController =
                new PIDFController(globalTelemetry, TRANSLATION_PIDF_COEFFICIENTS);
        this.movementMode = movementMode;
    }

    /// Toggles super slow mode
    public void toggleSuperSlow() {
        isSuperSlow = !isSuperSlow;
    }

    /// Applies the computed motor powers to the motors, then resets them.
    public void apply() {
        mecanumDrive.apply();
    }

    /// Rotates the robot using input from the *right* joystick of the gamepad.
    public void joystickRotate(Gamepad gamepad, boolean slow) {
        double turn = -gamepad.right_stick_x * speedMultiplier(slow);

        turn = MecanumDrive.smooth(turn);
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

    /// Turns the robot towards a target position. Returns true if still turning, false if finished.
    public boolean turnTowards(RobotPosition robotPosition, Position2D targetPos) {
        Position2D robotPos = robotPosition.getPosition();

        Distance dx = targetPos.getX().subtract(robotPos.getX());
        Distance dy = targetPos.getY().subtract(robotPos.getY());
        Angle targetDirection = Distance.atan2(dy, dx);

        return turnTowardsHeading(robotPosition, targetDirection);
    }

    /// Turns the robot towards a target heading. Returns true if still turning, false if finished.
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

    /// Translates the robot towards a target position. Returns true if still translating, false if
    /// finished.
    public boolean translateToPosition(RobotPosition robotPosition, Position2D targetPos) {
        Position2D robotPos = robotPosition.getPosition();

        DistanceUnit distanceUnit = DistanceUnit.INCH;

        Distance dx = targetPos.getX().subtract(robotPos.getX());
        Distance dy = targetPos.getY().subtract(robotPos.getY());
        double distanceError = Distance.hypot(dx, dy).getValue(distanceUnit);

        translationController.setError(distanceError);
        double speed = translationController.get();
        Vector2D velocity = new Vector2D(dx, dy).normalize().scale(speed);

        translate(new Translation(velocity.getX(distanceUnit), velocity.getY(distanceUnit)));

        boolean isFinished =
                translationController.isStableAtTarget(
                        TRANSLATION_TOLERANCE.getValue(distanceUnit),
                        NOT_TRANSLATING_THRESHOLD.getValue(distanceUnit));
        globalTelemetry.addData("Translation Speed", speed);
        globalTelemetry.addData("Error change", translationController.getErrorChange());
        return !isFinished;
    }

    /// Computes translation speed forward and sideways. Returns the pair (forward, strafe).
    private Translation getTranslationVelocity(Gamepad gamepad, boolean slow) {
        double forward = gamepad.left_stick_y * speedMultiplier(slow);
        forward = MecanumDrive.smooth(forward);

        double strafe = gamepad.left_stick_x * speedMultiplier(slow);
        strafe = MecanumDrive.smooth(strafe);

        return new Translation(forward, strafe);
    }

    /// Get movement speed multiplier
    private double speedMultiplier(boolean slow) {
        if (isSuperSlow) {
            return SUPER_SLOW_SPEED_MULTIPLIER;
        }
        return slow ? SLOW_SPEED_MULTIPLIER : SPEED_MULTIPLIER;
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
        mecanumDrive.move(forward, strafe, turnSpeed);
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        return mecanumDrive.getCurrentState();
    }

    @Override
    public void setState(HashMap<String, String> state) {
        mecanumDrive.setState(state);
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
