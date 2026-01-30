package opmodes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;

import logic.PlayingField;
import logic.Team;

import modules.sensor.GamepadController;

@Config
public class ManualOpMode extends OpModeBase {
    public ManualOpMode(Team team, boolean isAfterAuto, boolean calculatePose) {
        super(team, !isAfterAuto, calculatePose);
    }

    public ManualOpMode(Team team, boolean isAfterAuto) {
        this(team, isAfterAuto, true);
    }

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        runtime.reset();

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

    private void runStep() {
        update();

        executeActions();

        apply();
        log();
    }

    private void executeActions() {
        if (calculatePose && gamepad.isPressing(GamepadController.Button.BUMPER_LEFT)) {
            // Lock towards the goal
            move.lockedJoystickTranslate(
                    gamepad1,
                    gamepad.isPressing(GamepadController.Button.LEFT_STICK),
                    robotPosition,
                    team,
                    PlayingField.goalPos(team));
        } else {
            move.joystickTranslate(
                    gamepad1,
                    gamepad.isPressing(GamepadController.Button.LEFT_STICK),
                    robotPosition,
                    team);
            move.joystickRotate(gamepad1, gamepad.isPressing(GamepadController.Button.RIGHT_STICK));
        }

        if (gamepad.isPressed(GamepadController.Button.X)) cannon.toggle();

        // LED indication for cannon readiness
        double r = cannon.isReadyToShoot() ? 0.0 : 1.0;
        gamepad.gamepad.setLedColor(r, 1.0 - r, 0.0, Gamepad.LED_DURATION_CONTINUOUS);

        if (gamepad.isPressing(GamepadController.Button.TRIGGER_LEFT)) {
            intake.on();
            cannonBuffers.clear();
        } else {
            intake.off();
        }

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
        if (gamepad.isLongPressed(GamepadController.Button.B) && calculatePose)
            robotPosition.resetPose();
    }
}
