package opmodes;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import config.HardwareConfig;

import logic.Team;
import logic.field.PlayingField;

import math.Distance;

import modules.actuator.CannonCalibrator;
import modules.sensor.GamepadController;

public class CannonCalibrationOpMode extends OpModeBase {
    private CannonCalibrator cannonCalibrator;

    public CannonCalibrationOpMode(Team team) {
        super(team, true);
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

    @Override
    protected void initialize() {
        super.initialize();
        cannonCalibrator =
                new CannonCalibrator(
                        globalTelemetry,
                        hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_LEFT_ID),
                        hardwareMap.get(DcMotorEx.class, HardwareConfig.CANNON_MOTOR_RIGHT_ID));
    }

    private void runStep() {
        update();

        executeActions();

        apply();
        log();
        cannonCalibrator.printCalibrationData();
    }

    public void executeActions() {
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

        if (gamepad.isPressed(GamepadController.Button.X)) cannonCalibrator.toggle();
        if (gamepad.isPressed(GamepadController.Button.Y)) cannonCalibrator.speedup();
        if (gamepad.isLongPressed(GamepadController.Button.Y)) cannonCalibrator.fastSpeedup();
        if (gamepad.isPressed(GamepadController.Button.A)) cannonCalibrator.slowdown();
        if (gamepad.isLongPressed(GamepadController.Button.A)) cannonCalibrator.fastSlowdown();
        if (gamepad.isPressed(GamepadController.Button.B)) {
            Distance targetDistance =
                    PlayingField.distanceToGoal(robotPosition.getPosition(), team);
            cannonCalibrator.saveCurrentCalibrationData(targetDistance);
        }

        if (gamepad.isPressing(GamepadController.Button.TRIGGER_LEFT)) {
            intake.on();
            cannonBuffers.clear();
        } else {
            intake.off();
        }
    }

    public void runStop() {
        cannonCalibrator.printCalibrationData();
        globalTelemetry.update();
    }
}
