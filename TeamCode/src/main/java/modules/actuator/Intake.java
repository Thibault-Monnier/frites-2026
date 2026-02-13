package modules.actuator;

import static config.IntakeConfig.INTAKE_MOVING_SPEED;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

public class Intake implements RobotActuatorModule {

    private final Telemetry globalTelemetry;
    private final DcMotor motor;
    private double motorTargetPower;
    private boolean isRunning = false;
    private boolean isClearing = false;

    public Intake(Telemetry globalTelemetry, DcMotor motor) {
        this.globalTelemetry = globalTelemetry;
        this.motor = motor;
    }

    @Override
    public void apply() {
        update();
        motor.setPower(motorTargetPower);
    }

    /// Toggle intake motor on/off.
    public void toggle() {
        isRunning = !isRunning;
    }

    /// Turn intake motor on.
    public void on() {
        isRunning = true;
    }

    /// Turn intake motor off.
    public void off() {
        isRunning = false;
    }

    /// Set intake motor state.
    public void set(boolean isRunning) {
        this.isRunning = isRunning;
    }

    /// Clear the intake by running it in reverse for one cycle.
    public void clear() {
        off();
        isClearing = true;
    }

    /// Update motor power.
    private void update() {
        motorTargetPower = 0;
        if (isRunning) {
            motorTargetPower = INTAKE_MOVING_SPEED;
        } else if (isClearing) {
            motorTargetPower = -INTAKE_MOVING_SPEED;
            isClearing = false;
        }
        globalTelemetry.addData("Intake Motor Power", motorTargetPower);
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("isRunning", isRunning);
        return state;
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException("Intake module does not support state loading.");
    }
}
