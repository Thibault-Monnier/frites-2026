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
        if (gamepad.isPressing(GamepadController.Button.BUMPER_LEFT)) {
            // Lock towards the goal
            move.lockedJoystickMove(
                    gamepad1,
                    gamepad.isPressing(GamepadController.Button.LEFT_STICK),
                    PlayingField.goalPos(team));
        } else {
            move.joystickTranslate(
                    gamepad1, gamepad.isPressing(GamepadController.Button.LEFT_STICK));
            move.joystickRotate(gamepad1, gamepad.isPressing(GamepadController.Button.RIGHT_STICK));
        }

        if (gamepad.isPressed(GamepadController.Button.DPAD_UP)) move.initMoveToShoot();
        if (move.isMoving()) move.stopMacro();

        move.executeActiveMacro();

        if (gamepad.isPressed(GamepadController.Button.X)) cannon.toggle();

        // LED indication for cannon readiness
        double r = cannon.isReadyToShoot() ? 0.0 : 1.0;
        gamepad.gamepad.setLedColor(r, 1.0 - r, 0.0, Gamepad.LED_DURATION_CONTINUOUS);

        intake.set(gamepad.isPressing(GamepadController.Button.TRIGGER_LEFT));

        // Make sure the cannon reached its target velocity
        if ((gamepad.isPressing(GamepadController.Button.TRIGGER_RIGHT) && cannon.isReadyToShoot())
                || gamepad.isPressing(GamepadController.Button.BUMPER_RIGHT)) {
            cannonBuffers.shootContinue(true);
            intake.on();
        } else {
            cannonBuffers.shootDontContinue();

            if (gamepad.isPressing(GamepadController.Button.TRIGGER_RIGHT))
                gamepad.rumble(50); // Cannon isn't ready
            else cannonBuffers.shootReset();
        }

        if (gamepad.isPressing(GamepadController.Button.A)) {
            intake.clear();
            cannonBuffers.clear();
        }

        if (gamepad.isDoublePressed(GamepadController.Button.B)) move.toggleSuperSlow();
        if (gamepad.isLongPressed(GamepadController.Button.B)) robotPosition.resetPose();
    }
}
