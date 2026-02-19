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
import logic.pidf.PIDFController;
import logic.position.RobotPosition;

import math.Angle;
import math.Pose2D;
import math.Position2D;
import math.Vector2D;

import modules.actuator.MecanumDrive;
import modules.actuator.RobotActuatorModule;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

@Config
public class Movement implements RobotActuatorModule {
    private final Telemetry globalTelemetry;

    private final RobotPosition robotPosition;
    private final Team team;

    private final MecanumDrive mecanumDrive;

    private final PIDFController turnController;
    private final PIDFController translationController;

    private final MovementMode movementMode;
    private boolean isSuperSlow = false;

    private Macro activeMacro = Macro.NONE;

    public Movement(
            Telemetry globalTelemetry,
            RobotPosition robotPosition,
            Team team,
            DcMotor FL,
            DcMotor FR,
            DcMotor BL,
            DcMotor BR,
            MovementMode movementMode) {
        this.globalTelemetry = globalTelemetry;

        this.robotPosition = robotPosition;
        this.team = team;

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

    /// Returns whether the robot is currently moving
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
        translationController.setCoefficients(TRANSLATION_PIDF_COEFFICIENTS);
        globalTelemetry.addData("Turn coefficients", turnController.getCoefficients());
        globalTelemetry.addData(
                "Translation coefficients", translationController.getCoefficients());
    }

    /// Initializes a macro that moves the robot to the shooting position and rotates it to face the
    /// goal.
    public void initMoveToShoot() {
        activeMacro = Macro.MOVE_TO_SHOOT;
    }

    public void initMacro(Macro macro) {
        activeMacro = macro;
    }

    /// Initializes a macro that moves the robot to the parking position and rotates it to the
    /// correct heading for parking.
    public void initMoveToPark() {
        activeMacro = Macro.MOVE_TO_PARK;
    }

    /// Executes the active macro, if any. Returns true if the macro has finished and false
    /// otherwise.
    public boolean executeActiveMacro() {
        switch (activeMacro) {
            case MOVE_TO_SHOOT: {
                boolean doneTranslating =
                        translateToPosition(PlayingField.shootingPosition(team));
                boolean doneTurning = turnTowards(PlayingField.goalPos(team));

                boolean done = doneTurning && doneTranslating;
                if (done) stopMacro();
                return done;
            }
            case MOVE_TO_PARK: {
                Pose2D parkingPose = PlayingField.parkingPose(team);
                boolean doneTranslating = translateToPosition(parkingPose.toPosition2D());
                boolean doneTurning = turnTowardsHeading(parkingPose.getHeading());

                boolean done = doneTurning && doneTranslating;
                if (done) stopMacro();
                isSuperSlow = true;
                return done;
            }
            case MOVE_TO_FIRST_ARTIFACT_ROW:
                return moveToRow(PlayingField.firstArtifactRowEntryPose(team));

            case MOVE_TO_SECOND_ARTIFACT_ROW:
                return moveToRow(PlayingField.secondArtifactRowEntryPose(team));

            case MOVE_TO_THIRD_ARTIFACT_ROW:
                return moveToRow(PlayingField.thirdArtifactRowEntryPose(team));

            case COLLECT_FIRST_ARTIFACT_ROW:
                return moveToRow(PlayingField.firstArtifactRowCollectPose(team));
            case COLLECT_SECOND_ARTIFACT_ROW:
                return moveToRow(PlayingField.secondArtifactRowCollectPose(team));
            case COLLECT_THIRD_ARTIFACT_ROW:
                return moveToRow(PlayingField.thirdArtifactRowCollectPose(team));


            case NONE:
                return true;
        }
        throw new AssertionError("Unhandled macro: " + activeMacro);
    }

    private boolean moveToRow(Pose2D pose) {
        boolean doneTranslating = translateToPosition(pose.toPosition2D());
        boolean doneTurning = turnTowardsHeading(pose.getHeading());

        boolean done = doneTurning && doneTranslating;
        if (done) stopMacro();
        return done;
    }

    /// Stops any active macro, returning control to the driver.
    public void stopMacro() {
        activeMacro = Macro.NONE;
    }

    /// Rotates the robot using input from the *right* joystick of the gamepad.
    public void joystickRotate(Gamepad gamepad, boolean slow) {
        double turn = -gamepad.right_stick_x * speedMultiplier(slow);

        turn = MecanumDrive.smooth(turn);
        turn(turn);
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

        turnTowards(targetPos);

        if (movementMode == MovementMode.FIELD_CENTRIC) {
            translateFieldCentric(velocity);
        } else {
            translate(velocity);
        }
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

        double turnSpeed = turnController.get();
        turn(turnSpeed);

        boolean isFinished =
                turnController.isStableAtTarget(
                        TURN_TOLERANCE.toRadians(), NOT_TURNING_THRESHOLD.toRadians());
        globalTelemetry.addData("Turn Speed", turnSpeed);
        globalTelemetry.addData("Error change", turnController.getErrorChange());
        return isFinished;
    }

    /// Translates the robot towards a target position. Returns true if finished, false otherwise.
    public boolean translateToPosition(Position2D targetPos) {
        Position2D robotPos = robotPosition.getPosition();
        Position2D delta = targetPos.subtract(robotPos);

        DistanceUnit errorUnit = DistanceUnit.MM;

        double distanceError = delta.hypot().getValue(errorUnit);
        translationController.setError(distanceError);
        double speed = translationController.get();

        Vector2D velocity = new Vector2D(delta).normalizeMax().scale(speed);

        Angle robotAngle = robotPosition.getPose().getHeading();
        Translation translation = new Translation(velocity.getRawX(), velocity.getRawY());
        translateFieldCentric(robotAngle, translation);

        boolean isFinished =
                translationController.isStableAtTarget(
                        TRANSLATION_TOLERANCE.getValue(errorUnit),
                        NOT_TRANSLATING_THRESHOLD.getValue(errorUnit));
        globalTelemetry.addData("Translation speed", speed);
        globalTelemetry.addData("Translation velocity", velocity.toString());
        globalTelemetry.addData("Error change", translationController.getErrorChange());
        return isFinished;
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
        Angle robotAngle = robotPosition.getPose().getHeading();

        Angle delta = Angle.fromDegrees(90);
        if (team.isBlue()) robotAngle = robotAngle.subtract(delta);
        if (team.isRed()) robotAngle = robotAngle.add(delta);

        translateFieldCentric(robotAngle, translation);
    }

    private void translateFieldCentric(Angle robotAngle, Translation translation) {
        double heading = robotAngle.toRadians();

        double forward = translation.forward;
        double strafe = translation.strafe;

        translation.forward = -forward * Math.cos(heading) - strafe * Math.sin(heading);
        translation.strafe = forward * Math.sin(heading) - strafe * Math.cos(heading);

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

    public void waitForMacro() {
        while (executeActiveMacro()) {
        }
    }

    public enum MovementMode {
        ROBOT_CENTRIC,
        FIELD_CENTRIC
    }

    public enum Macro {
        MOVE_TO_SHOOT,
        MOVE_TO_PARK,
        MOVE_TO_FIRST_ARTIFACT_ROW,
        MOVE_TO_SECOND_ARTIFACT_ROW,
        MOVE_TO_THIRD_ARTIFACT_ROW,
        COLLECT_FIRST_ARTIFACT_ROW,
        COLLECT_SECOND_ARTIFACT_ROW,
        COLLECT_THIRD_ARTIFACT_ROW,
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
