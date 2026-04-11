package modules.actuator.cannonBuffer;

import static config.CannonConfig.MOVING_SPEED;
import static config.CannonConfig.REVERSE_SPEED;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import modules.actuator.RobotActuatorModule;

import java.util.HashMap;

public class CannonBuffer implements RobotActuatorModule {
    private final DcMotor motor;
    private boolean isRunning = false;
    private boolean isReversing = false;

    public CannonBuffer(DcMotor motor, DcMotorSimple.Direction direction) {
        this.motor = motor;
        this.motor.setDirection(direction);

        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void apply() {
        double servoTargetPower = 0;
        if (isRunning) {
            servoTargetPower = MOVING_SPEED;
        } else if (isReversing) {
            servoTargetPower = REVERSE_SPEED;
            isReversing = false;
        }
        motor.setPower(servoTargetPower);
    }

    /// Turn buffer motor off.
    public void off() {
        isRunning = false;
        isReversing = false;
    }

    /// Turn buffer motor on.
    public void on() {
        off();
        isRunning = true;
    }

    /// Clears the buffer by running it in reverse for one cycle.
    public void reverse() {
        off();
        isReversing = true;
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("isRunning", isRunning);
        state.put("isReversing", isReversing);
        return state;
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException("Cannon module does not support state loading.");
    }
}
