package opmodes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;

import config.ManualOpModeMappings;

import logic.Movement.Macro;
import logic.Team;
import logic.field.PlayingField;

import modules.sensor.GamepadController;

import utils.Angle;

@Config
public class ManualOpMode extends OpModeBase {
    boolean isChildMode = false;

    public ManualOpMode(Team team, boolean isAfterAuto) {
        super(team, false, !isAfterAuto);
    }

    public ManualOpMode(Team team, boolean isAfterAuto, boolean isChildMode) {
        super(team, false, !isAfterAuto);
        this.isChildMode = isChildMode;
    }

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        runStart();

        double prevTime = runtime.milliseconds();
        while (opModeIsActive()) {
            // Consistent step duration for better PIDs
            double time = runtime.milliseconds();
            globalTelemetry.addData("Delta time", time - prevTime);
            while (time - prevTime < 35) {
                time = runtime.milliseconds();
            }
            prevTime = time;

            runStep();
        }

        runStop();
    }

    private void runStep() {
        update();

        executeActions();

        apply();
        log();
    }

    private void executeActions() {
        if (isPressActive(ManualOpModeMappings.DRIVE_MODE_TOGGLE)) move.toggleMovementMode();
        if (isPressActive(ManualOpModeMappings.LOCK_TOWARDS_SHOOT_TOGGLE))
            move.toggleLockTowardsGoal();

        move.joystickTranslate(
                gamepadController.gamepad, isPressActive(ManualOpModeMappings.SLOW_MOVE));

        if (isPressActive(ManualOpModeMappings.LOCK_TOWARDS_SHOOT) && !move.lockingTowardsGoal())
            move.turnTowardsHeading(shotHandler.getShotAngle());
        else move.rotate(gamepadController.gamepad, isPressActive(ManualOpModeMappings.SLOW_TURN));

        if (isPressActive(ManualOpModeMappings.MOVE_TO_SHOOTING_SPOT))
            move.initMacro(Macro.MOVE_TO_SHOOT);
        else if (isPressActive(ManualOpModeMappings.MOVE_TO_RAMP_SPOT))
            move.initMacro(Macro.MOVE_TO_RAMP);
        else if (isPressActive(ManualOpModeMappings.MOVE_TO_RAMP_DEFENSE_SPOT))
            move.initMacro(Macro.MOVE_TO_RAMP_DEFENSE);
        else if (isPressActive(ManualOpModeMappings.MOVE_TO_PARKING_SPOT)) {
            move.initMacro(Macro.MOVE_TO_PARK);
            cannon.off();
        }
        if (move.isMoving()) move.stopMacro();

        move.executeActiveMacro();

        if (isPressActive(ManualOpModeMappings.CANNON_ON_OFF_TOGGLE)) cannon.toggle();

        if (cannon.isReadyToShoot()) gamepadController.ledGreen(Gamepad.LED_DURATION_CONTINUOUS);
        else gamepadController.ledRed(Gamepad.LED_DURATION_CONTINUOUS);

        intake.off();

        boolean allowedToShoot = true;
        if (isChildMode) {
            Angle threshold = Angle.fromDegrees(15);
            Angle angle =
                    robotPosition.getPose().toPosition2D().angleTo(PlayingField.goalPos(team));
            Angle angleDiff = angle.subtract(robotPosition.getHeading());
            if (!angleDiff.abs().leq(threshold)) allowedToShoot = false;
        }

        // Make sure the cannon reached its target velocity
        if (allowedToShoot
                && ((isPressActive(ManualOpModeMappings.SHOOT) && cannon.isReadyToShoot())
                        || (isPressActive(ManualOpModeMappings.FORCE_SHOOT) && !isChildMode))) {
            // If red, start right
            cannonBuffers.shootContinue(team.isBlue());
            intake.on();
        } else {
            cannonBuffers.shootDontContinue();

            if (isPressActive(ManualOpModeMappings.SHOOT))
                gamepadController.rumble(50); // Cannon isn't ready
            else cannonBuffers.shootReset();
        }

        if (isPressActive(ManualOpModeMappings.INTAKE_ON)) {
            intake.on();
            cannonBuffers.reverse();
        }

        if (isPressActive(ManualOpModeMappings.INTAKE_AND_TRANSFER_REVERSE)) {
            intake.reverse();
            cannonBuffers.reverse();
        }

        if (isPressActive(ManualOpModeMappings.SUPER_SLOW_MODE_TOGGLE)) move.toggleSuperSlow();
        if (isPressActive(ManualOpModeMappings.RESET_ROBOT_POSE)) robotPosition.resetPose();
    }

    /** Returns true if the button mapping is active based on its press type. */
    private boolean isPressActive(GamepadController.ButtonMapping mapping) {
        return gamepadController.isPressActive(mapping);
    }
}
