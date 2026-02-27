package modules.actuator.intake;

import static config.IntakeConfig.INTAKE_MOVING_SPEED;

import com.qualcomm.robotcore.hardware.DcMotor;

import modules.actuator.RobotActuatorModule;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

public class Intake implements RobotActuatorModule {
    private final Telemetry globalTelemetry;
    private final DcMotor motor;
    private boolean isRunning = false;
    private boolean isReversing = false;

    public Intake(Telemetry globalTelemetry, DcMotor motor) {
        this.globalTelemetry = globalTelemetry;
        this.motor = motor;
    }

    @Override
    public void apply() {
        double motorTargetPower = 0;
        if (isRunning) {
            motorTargetPower = INTAKE_MOVING_SPEED;
        } else if (isReversing) {
            motorTargetPower = -INTAKE_MOVING_SPEED;
            isReversing = false;
        }

        globalTelemetry.addData("Intake Motor Power", motorTargetPower);
        motor.setPower(motorTargetPower);
    }

    /// Turn intake motor off.
    public void off() {
        isRunning = false;
        isReversing = false;
    }

    /// Turn intake motor on.
    public void on() {
        off();
        isRunning = true;
    }

    /// Clears the intake by running it in reverse for one cycle.
    public void reverse() {
        off();
        isReversing = true;
    }

    /// Toggle intake motor on/off.
    public void toggle() {
        isRunning = !isRunning;
    }

    /// Set intake motor state.
    public void set(boolean isRunning) {
        this.isRunning = isRunning;
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
