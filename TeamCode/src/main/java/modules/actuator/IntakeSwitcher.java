package modules.actuator;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

@Config
public class IntakeSwitcher implements RobotActuatorModule {
    private final Telemetry globalTelemetry;
    private final Servo servo;

    private Position currentPosition = Position.CENTER;

    private static final double CENTER_POS = 0.49;
    private static final double OFFSET = 0.105;

    public IntakeSwitcher(Telemetry globalTelemetry, Servo servo) {
        this.globalTelemetry = globalTelemetry;
        this.servo = servo;

        servo.setPosition(CENTER_POS);
    }

    @Override
    public void apply() {
        if (currentPosition == Position.CENTER) {
            servo.setPosition(CENTER_POS);
        } else if (currentPosition == Position.LEFT) {
            servo.setPosition(CENTER_POS - OFFSET);
        } else {
            servo.setPosition(CENTER_POS + OFFSET);
        }

        globalTelemetry.addData("Intake Switcher Position", servo.getPosition());
    }

    public void toggle() {
        if (currentPosition == Position.LEFT) {
            currentPosition = Position.RIGHT;
        } else {
            currentPosition = Position.LEFT;
        }
    }

    /// Sets the intake switcher to the center position.
    public void center() {
        currentPosition = Position.CENTER;
    }

    /// Sets the intake switcher to the left position.
    public void left() {
        currentPosition = Position.LEFT;
    }

    /// Sets the intake switcher to the right position.
    public void right() {
        currentPosition = Position.RIGHT;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("currentPosition", currentPosition.toString());
        state.put("servoPosition", servo.getPosition());
        return state;
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException("Cannon module does not support state loading.");
    }

    public enum Position {
        LEFT,
        CENTER,
        RIGHT
    }
}
