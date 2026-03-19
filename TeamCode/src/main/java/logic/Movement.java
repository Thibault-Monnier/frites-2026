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

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import logic.field.PlayingField;
import logic.pidf.PIDFLController;
import logic.position.RobotPosition;

import math.Angle;
import math.Pose2D;
import math.Position2D;
import math.Vector2D;

import modules.actuator.RobotActuatorModule;
import modules.actuator.drive.MecanumDrive;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

@Config
public class Movement implements RobotActuatorModule {
    private final Telemetry globalTelemetry;

    private final RobotPosition robotPosition;
    private final Team team;

    private final ShotHandler shotHandler;

    private final MecanumDrive mecanumDrive;

    private final PIDFLController turnController;
    private final PIDFLController translationXController;
    private final PIDFLController translationYController;

    private MovementMode movementMode;
    private boolean isSuperSlow = false;
    private boolean lockTowardGoal = false;

    private Macro activeMacro = Macro.NONE;

    public Movement(
            Telemetry globalTelemetry,
            RobotPosition robotPosition,
            ShotHandler shotHandler,
            Team team,
            DcMotor FL,
            DcMotor FR,
            DcMotor BL,
            DcMotor BR,
            MovementMode movementMode) {
        this.globalTelemetry = globalTelemetry;

        this.robotPosition = robotPosition;
        this.team = team;

        this.shotHandler = shotHandler;

        this.mecanumDrive = new MecanumDrive(globalTelemetry, FL, FR, BL, BR);
        this.turnController = new PIDFLController(globalTelemetry, TURN_PIDF_COEFFICIENTS);
        this.translationXController =
                new PIDFLController(globalTelemetry, TRANSLATION_PIDF_COEFFICIENTS);
        this.translationYController =
                new PIDFLController(globalTelemetry, TRANSLATION_PIDF_COEFFICIENTS);
        this.movementMode = movementMode;
    }

    /// Toggles the movement mode between field centric and robot centric.
    public void toggleMovementMode() {
        if (movementMode == MovementMode.FIELD_CENTRIC) movementMode = MovementMode.ROBOT_CENTRIC;
        else if (movementMode == MovementMode.ROBOT_CENTRIC)
            movementMode = MovementMode.FIELD_CENTRIC;
        else {
            throw new RuntimeException("Unimplemented state in toggleMovementMode");
        }
    }

    /// Toggles super slow mode.
    public void toggleSuperSlow() {
        isSuperSlow = !isSuperSlow;
    }

    public void toggleLockTowardsGoal() {
        lockTowardGoal = !lockTowardGoal;
    }

    public boolean lockingTowardsGoal() {
        return lockTowardGoal;
    }

    /// Returns whether the robot is currently moving.
    public boolean isMoving() {
        return mecanumDrive.isMoving();
    }

    /// Applies the computed motor powers to the motors, then resets them.
    public void apply() {
        mecanumDrive.apply();
    }

    /// Reloads the translation controller coefficients from the config. Useful for tuning the PIDF
    /// controller coefficients using FTC Dashboard.
    public void reloadPIDFCoefficients() {
        turnController.setCoefficients(TURN_PIDF_COEFFICIENTS);
        translationXController.setCoefficients(TRANSLATION_PIDF_COEFFICIENTS);
        translationYController.setCoefficients(TRANSLATION_PIDF_COEFFICIENTS);

        globalTelemetry.addData("Turn coefficients", turnController.getCoefficients());
        globalTelemetry.addData(
                "Translation X coefficients", translationXController.getCoefficients());
        globalTelemetry.addData(
                "Translation Y coefficients", translationYController.getCoefficients());
    }

    /// Sets the provided macro as the active macro.
    public void initMacro(Macro macro) {
        activeMacro = macro;
    }

    /// Executes the active macro, if any. Returns true if the macro has finished and false
    /// otherwise.
    public boolean executeActiveMacro() {
        if (activeMacro == Macro.NONE) return true;

        Position2D targetPos;
        Angle targetHeading;

        switch (activeMacro) {
            case MOVE_TO_SHOOT:
                {
                    targetPos = PlayingField.shootingPosition(team);
                    targetHeading = targetPos.angleTo(PlayingField.goalPos(team));
                }
                break;
            case MOVE_TO_PARK:
                {
                    Pose2D targetPose = PlayingField.parkingPose(team);
                    targetPos = targetPose.toPosition2D();
                    targetHeading = targetPose.getHeading();
                }
                isSuperSlow = true;
                break;
            case MOVE_TO_RAMP:
                {
                    Pose2D targetPose = PlayingField.rampPose(team);
                    targetPos = targetPose.toPosition2D();
                    targetHeading = targetPose.getHeading();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unhandled macro: " + activeMacro);
        }

        boolean done = translateToPosition(targetPos);
        done &= turnTowardsHeading(targetHeading);

        if (done) stopMacro();
        return done;
    }

    /// Stops any active macro, returning control to the driver.
    public void stopMacro() {
        activeMacro = Macro.NONE;
    }

    /// Rotates the robot using input from the *right* joystick of the gamepad. If locking towards
    /// goal, rotate to face the goal instead.
    public void rotate(Gamepad gamepad, boolean slow) {
        double turn = -gamepad.right_stick_x * speedMultiplier(slow);

        if (Math.abs(turn) > 0.1) {
            turn = MecanumDrive.smooth(turn);
            turn(turn);
            lockTowardGoal = false;
        } else if (lockTowardGoal) {
            turnTowardsHeading(shotHandler.getShotAngle());
            lockTowardGoal = true; // Should not get overridden
        }
    }

    /// Translates the robot using input from the *left* joystick of the gamepad.
    public void joystickTranslate(Gamepad gamepad, boolean slow) {
        Translation velocity = getTranslationVelocity(gamepad, slow);

        if (movementMode == MovementMode.FIELD_CENTRIC) {
            translateFieldCentric(velocity);
        } else {
            translate(velocity);
        }
    }

    /// Moves while turning towards a target position.
    public void lockedJoystickMove(Gamepad gamepad, boolean slow, Position2D targetPos) {
        Translation velocity = getTranslationVelocity(gamepad, slow);
        if (movementMode == MovementMode.FIELD_CENTRIC) {
            translateFieldCentric(velocity);
        } else {
            translate(velocity);
        }

        turnTowards(targetPos);
    }

    /// Turns the robot towards a target position. Returns true if finished, false otherwise.
    public boolean turnTowards(Position2D targetPos) {
        Position2D robotPos = robotPosition.getPosition();
        Angle targetDirection = targetPos.subtract(robotPos).direction();
        return turnTowardsHeading(targetDirection);
    }

    /// Turns the robot towards a target heading. Returns true finished, false otherwise.
    public boolean turnTowardsHeading(Angle targetHeading) {
        Pose2D robotPose = robotPosition.getPose();

        Angle angleError = targetHeading.subtract(robotPose.getHeading());
        turnController.setError(angleError.toRadians());

        globalTelemetry.addData("Turn error", angleError.toString());
        globalTelemetry.addData("Turn error change", turnController.getErrorChange());

        boolean isFinished =
                turnController.isStableAtTarget(
                        TURN_TOLERANCE.toRadians(), NOT_TURNING_THRESHOLD.toRadians());
        if (isFinished) {
            globalTelemetry.addLine("Turn controller is finished");
            return true;
        }

        double turnSpeed = turnController.get();
        turn(turnSpeed);

        globalTelemetry.addData("Turn speed", turnSpeed);

        return false;
    }

    /// Translates the robot towards a target position. Returns true if finished, false otherwise.
    public boolean translateToPosition(Position2D targetPos) {
        Position2D robotPos = robotPosition.getPosition();
        Vector2D error = targetPos.subtract(robotPos).toVector2D();

        DistanceUnit errorUnit = DistanceUnit.MM;

        double xError = error.getX(errorUnit);
        double yError = error.getY(errorUnit);

        translationXController.setError(xError);
        translationYController.setError(yError);

        boolean isFinished =
                translationXController.isStableAtTarget(
                                TRANSLATION_TOLERANCE.getValue(errorUnit),
                                NOT_TRANSLATING_THRESHOLD.getValue(errorUnit))
                        && translationYController.isStableAtTarget(
                                TRANSLATION_TOLERANCE.getValue(errorUnit),
                                NOT_TRANSLATING_THRESHOLD.getValue(errorUnit));
        if (isFinished) {
            globalTelemetry.addLine("Translation controller is finished");
            return true;
        }

        double dx = translationXController.get();
        double dy = translationYController.get();

        Angle robotAngle = robotPosition.getHeading();
        Translation translation = new Translation(dx, dy);
        translateFieldCentric(robotAngle, translation);

        globalTelemetry.addData("X Error", xError);
        globalTelemetry.addData("Y Error", yError);
        globalTelemetry.addData("Dx", dx);
        globalTelemetry.addData("Dy", dy);

        return false;
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

    private void translateFieldCentric(Translation translation) {
        Angle robotAngle = robotPosition.getHeading();

        Angle delta = Angle.fromDegrees(90);
        if (team.isBlue()) robotAngle = robotAngle.subtract(delta);
        if (team.isRed()) robotAngle = robotAngle.add(delta);

        translateFieldCentric(robotAngle, translation);
    }

    private void translateFieldCentric(Angle heading, Translation translation) {
        double forward = translation.forward;
        double strafe = translation.strafe;

        translation.forward = -forward * heading.cos() - strafe * heading.sin();
        translation.strafe = forward * heading.sin() - strafe * heading.cos();

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

    public enum Macro {
        MOVE_TO_SHOOT,
        MOVE_TO_PARK,
        MOVE_TO_RAMP,
        NONE
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
