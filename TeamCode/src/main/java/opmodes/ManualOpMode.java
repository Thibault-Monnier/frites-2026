package opmodes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;

import config.ManualOpModeMappings;

import logic.Movement.Macro;
import logic.Team;
import logic.field.PlayingField;

import modules.sensor.GamepadController;

@Config
public class ManualOpMode extends OpModeBase {
    public ManualOpMode(Team team, boolean isAfterAuto) {
        super(team, !isAfterAuto);
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
    }

    @Override
    protected void runStart() {
        super.runStart();
        cannon.on();
    }

    private void runStep() {
        update();

        executeActions();

        apply();
        log();
    }

    private void executeActions() {
        if (isPressActive(ManualOpModeMappings.MOVING_SHOT_TOGGLE))
            shotHandler.toggleUsingMovingShot();
        if (isPressActive(ManualOpModeMappings.DRIVE_MODE_TOGGLE)) move.toggleMovementMode();

        move.joystickTranslate(
                gamepadController.gamepad, isPressActive(ManualOpModeMappings.SLOW_MOVE));
        if (isPressActive(ManualOpModeMappings.LOCK_TOWARDS_SHOOT))
            move.turnTowardsHeading(shotHandler.getShotAngle());
        else
            move.joystickRotate(
                    gamepadController.gamepad, isPressActive(ManualOpModeMappings.SLOW_TURN));

        if (isPressActive(ManualOpModeMappings.MOVE_TO_SHOOTING_SPOT))
            move.initMacro(Macro.MOVE_TO_SHOOT);
        else if (isPressActive(ManualOpModeMappings.MOVE_TO_RAMP_SPOT))
            move.initMacro(Macro.MOVE_TO_RAMP);
        else if (isPressActive(ManualOpModeMappings.MOVE_TO_PARKING_SPOT)) {
            move.initMacro(Macro.MOVE_TO_PARK);
            cannon.off();
        }
        if (move.isMoving()) move.stopMacro();

        move.executeActiveMacro();

        if (isPressActive(ManualOpModeMappings.CANNON_ON_OFF_TOGGLE)) cannon.toggle();

        if (cannon.isReadyToShoot()) gamepadController.ledGreen(Gamepad.LED_DURATION_CONTINUOUS);
        else gamepadController.ledRed(Gamepad.LED_DURATION_CONTINUOUS);

        if (isPressActive(ManualOpModeMappings.INTAKE_ON)
                || PlayingField.isNextToRamp(robotPosition.getPose(), team)) {
            intake.on();
            cannonBuffers.reverse();
        } else {
            intake.off();
        }

        if (isPressActive(ManualOpModeMappings.INTAKE_AND_TRANSFER_REVERSE)) {
            intake.reverse();
            cannonBuffers.reverse();
        }

        // Make sure the cannon reached its target velocity
        if ((isPressActive(ManualOpModeMappings.SHOOT) && cannon.isReadyToShoot())
                || isPressActive(ManualOpModeMappings.FORCE_SHOOT)) {
            cannonBuffers.shootContinue(true);
            intake.on();
        } else {
            cannonBuffers.shootDontContinue();

            if (isPressActive(ManualOpModeMappings.SHOOT))
                gamepadController.rumble(50); // Cannon isn't ready
            else cannonBuffers.shootReset();
        }

        if (isPressActive(ManualOpModeMappings.SUPER_SLOW_MODE_TOGGLE)) move.toggleSuperSlow();
        if (isPressActive(ManualOpModeMappings.RESET_ROBOT_POSE)) robotPosition.resetPose();
    }

    /** Returns true if the button mapping is active based on its press type. */
    private boolean isPressActive(GamepadController.ButtonMapping mapping) {
        globalTelemetry.addData(
                "Is " + mapping.toString() + " active", gamepadController.isPressActive(mapping));
        return gamepadController.isPressActive(mapping);
    }
}
