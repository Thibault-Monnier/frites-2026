package opmodes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;

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
        if (gamepadController.isPressing(GamepadController.Button.BUMPER_LEFT)) {
            // Lock towards the goal
            move.lockedJoystickMove(
                    gamepadController.gamepad,
                    gamepadController.isPressing(GamepadController.Button.LEFT_STICK),
                    PlayingField.goalPos(team));
        } else {
            move.joystickTranslate(
                    gamepadController.gamepad,
                    gamepadController.isPressing(GamepadController.Button.LEFT_STICK));
            move.joystickRotate(
                    gamepadController.gamepad,
                    gamepadController.isPressing(GamepadController.Button.RIGHT_STICK));
        }

        if (gamepadController.isPressed(GamepadController.Button.DPAD_UP)) move.initMoveToShoot();
        if (move.isMoving()) move.stopMacro();

        move.executeActiveMacro();

        if (gamepadController.isPressed(GamepadController.Button.X)) cannon.toggle();

        if (cannon.isReadyToShoot()) gamepadController.ledGreen(Gamepad.LED_DURATION_CONTINUOUS);
        else gamepadController.ledRed(Gamepad.LED_DURATION_CONTINUOUS);

        intake.set(gamepadController.isPressing(GamepadController.Button.TRIGGER_LEFT));

        // Make sure the cannon reached its target velocity
        if ((gamepadController.isPressing(GamepadController.Button.TRIGGER_RIGHT)
                        && cannon.isReadyToShoot())
                || gamepadController.isPressing(GamepadController.Button.BUMPER_RIGHT)) {
            cannonBuffers.shootContinue(true);
            intake.on();
        } else {
            cannonBuffers.shootDontContinue();

            if (gamepadController.isPressing(GamepadController.Button.TRIGGER_RIGHT))
                gamepadController.rumble(50); // Cannon isn't ready
            else cannonBuffers.shootReset();
        }

        if (gamepadController.isPressing(GamepadController.Button.A)) {
            intake.clear();
            cannonBuffers.clear();
        }

        if (gamepadController.isDoublePressed(GamepadController.Button.B)) move.toggleSuperSlow();
        if (gamepadController.isLongPressed(GamepadController.Button.B)) robotPosition.resetPose();
    }
}
