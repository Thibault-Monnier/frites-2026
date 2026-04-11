package opmodes;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import config.HardwareConfig;
import config.ManualOpModeMappings;

import logic.Team;
import logic.field.PlayingField;

import modules.actuator.cannon.CannonCalibrator;
import modules.sensor.GamepadController;

import utils.TelemetryHandler;
import utils.math.Distance;

public class CannonCalibrationOpMode extends OpModeBase {
    public CannonCalibrationOpMode(Team team) {
        super(team, false, true);
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
            TelemetryHandler.addData("Delta time", time - prevTime);
            while (time - prevTime < 35) {
                time = runtime.milliseconds();
            }
            prevTime = time;

            runStep();
        }

        runStop();
    }

    @Override
    protected void initialize() {
        super.initialize();
        cannon =
                new CannonCalibrator(
                        hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_LEFT_ID),
                        hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_RIGHT_ID));
    }

    private void runStep() {
        update();

        executeActions();

        apply();
        log();
        cannon().printCalibrationData();
    }

    public void executeActions() {
        if (gamepadController.isPressing(GamepadController.Button.BUMPER_LEFT)) {
            // Lock towards the goal
            move.lockedJoystickMove(
                    gamepad1,
                    gamepadController.isPressing(GamepadController.Button.LEFT_STICK),
                    PlayingField.goalPos(team));
        } else {
            move.joystickTranslate(
                    gamepad1, gamepadController.isPressing(GamepadController.Button.LEFT_STICK));
            move.rotate(
                    gamepad1, gamepadController.isPressing(GamepadController.Button.RIGHT_STICK));
        }

        if ((isPressActive(ManualOpModeMappings.SHOOT) && cannon.isReadyToShoot())
                || isPressActive(ManualOpModeMappings.FORCE_SHOOT)) {
            cannonBuffers.shootContinue(true, cannon.isReadyToShoot());
            intake.on();
        } else {
            cannonBuffers.shootDontContinue();

            if (isPressActive(ManualOpModeMappings.SHOOT))
                gamepadController.rumble(50); // Cannon isn't ready
            else cannonBuffers.shootReset();
        }

        if (gamepadController.isPressed(GamepadController.Button.X)) cannon.toggle();
        if (gamepadController.isPressed(GamepadController.Button.Y)) cannon().speedup();
        if (gamepadController.isLongPressed(GamepadController.Button.Y)) cannon().fastSpeedup();
        if (gamepadController.isPressed(GamepadController.Button.A)) cannon().slowdown();
        if (gamepadController.isLongPressed(GamepadController.Button.A)) cannon().fastSlowdown();
        if (gamepadController.isPressed(GamepadController.Button.B)) {
            Distance targetDistance =
                    PlayingField.distanceToGoal(robotPosition.getPosition(), team);
            cannon().saveCurrentCalibrationData(targetDistance);
        }
        if (gamepadController.isLongPressed(GamepadController.Button.B)) {
            cannon().clearLastCalibrationData();
        }

        if (gamepadController.isPressing(GamepadController.Button.TRIGGER_LEFT)) {
            intake.on();
            cannonBuffers.reverse();
        } else {
            intake.off();
        }
    }

    private CannonCalibrator cannon() {
        return (CannonCalibrator) cannon;
    }

    private boolean isPressActive(GamepadController.ButtonMapping mapping) {
        return gamepadController.isPressActive(mapping);
    }

    @Override
    public void runStop() {
        super.runStop();
        cannon().printCalibrationData();
        TelemetryHandler.update();
    }
}
