package core.modules;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

public class CannonBuffer implements RobotModule {
    private final Telemetry globalTelemetry;
    private final CRServo servo;
    private boolean isRunning = false;

    public CannonBuffer(
            Telemetry globalTelemetry, CRServo servo, DcMotorSimple.Direction direction) {
        this.globalTelemetry = globalTelemetry;
        this.servo = servo;

        servo.setDirection(direction);
    }

    @Override
    public void apply() {
        if (isRunning) {
            servo.setPower(1.0); // Full speed
        } else {
            servo.setPower(0); // Stop
        }
    }

    public void on() {
        isRunning = true;
    }

    public void off() {
        isRunning = false;
    }

    public void toggle() {
        isRunning = !isRunning;
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        throw new UnsupportedOperationException("Cannon module does not support state saving.");
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException("Cannon module does not support state loading.");
    }
}
