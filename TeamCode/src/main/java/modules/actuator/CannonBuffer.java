package modules.actuator;

import static config.CannonConfig.MOVING_SPEED;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

public class CannonBuffer implements RobotActuatorModule {
    private final Telemetry globalTelemetry;
    private final CRServo servo;
    private boolean isRunning = false;
    private boolean isClearing = false;
    private boolean isReversing = false;

    public CannonBuffer(
            Telemetry globalTelemetry, CRServo servo, DcMotorSimple.Direction direction) {
        this.globalTelemetry = globalTelemetry;
        this.servo = servo;

        servo.setDirection(direction);
    }

    @Override
    public void apply() {
        double servoTargetPower = 0;
        if (isReversing) {
            servoTargetPower = -MOVING_SPEED;
        } else if (isRunning) {
            servoTargetPower = MOVING_SPEED;
        } else if (isClearing) {
            servoTargetPower = -MOVING_SPEED;
            isClearing = false;
        }
        servo.setPower(servoTargetPower);
    }

    /// Turn buffer servo on.
    public void on() {
        isRunning = true;
    }

    /// Turn buffer servo off.
    public void off() {
        isRunning = false;
        isReversing = false;
    }

    /// Clears the buffer by running it in reverse for one cycle.
    public void clear() {
        off();
        isClearing = true;
    }

    public void reverse() {
        off();
        isReversing = true;
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("isRunning", isRunning);
        state.put("isReversing", isReversing);
        state.put("isClearing", isClearing);
        return state;
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException("Cannon module does not support state loading.");
    }
}
