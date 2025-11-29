package robot;

import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import models.RobotModule;

import java.util.HashMap;

public class IntakeSwitcher implements RobotModule {
    private Telemetry globalTelemetry;
    private Servo servo;
    private boolean isAtLeftPos = false;
    private boolean isAtCenter = true;

    private final float CENTER_POS = 0.5F;
    private final float OFFSET = 0.125F;

    public IntakeSwitcher(Telemetry globalTelemetry, Servo servo) {
        this.globalTelemetry = globalTelemetry;
        this.servo = servo;

        servo.setPosition(CENTER_POS);
    }
    @Override
    public void apply() {
        if (isAtCenter) {
            servo.setPosition(CENTER_POS);
        } else {
            if (isAtLeftPos) {
                servo.setPosition(CENTER_POS - OFFSET);
            } else {
                servo.setPosition(CENTER_POS + OFFSET);
            }
        }

        double pos = servo.getPosition();

        globalTelemetry.addData("SERVO POSITION", pos);
    }

    public void toggle() {
        isAtCenter = false;
        isAtLeftPos = !isAtLeftPos;
    }

    public void center() {
        isAtCenter = true;
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
