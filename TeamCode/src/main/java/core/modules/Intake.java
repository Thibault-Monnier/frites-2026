package core.modules;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

public class Intake implements RobotModule {

    private final Telemetry globalTelemetry;
    private final DcMotor motor;

    private static final double MOVING_SPEED = -1.0f;
    private double motorTargetPower;
    private boolean isRunning = false;

    public Intake(Telemetry globalTelemetry, DcMotor motor) {
        this.globalTelemetry = globalTelemetry;
        this.motor = motor;
    }

    @Override
    public void apply() {
        motor.setPower(motorTargetPower);
    }

    /// Toggle intake motor on/off.
    public void toggle() {
        isRunning = !isRunning;
        update();

        globalTelemetry.addData("Intake Motor Power", motorTargetPower);
    }

    /// Update motor power.
    private void update() {
        motorTargetPower = isRunning ? MOVING_SPEED : 0;
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        throw new UnsupportedOperationException("Intake module does not support state saving.");
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException("Intake module does not support state loading.");
    }
}
