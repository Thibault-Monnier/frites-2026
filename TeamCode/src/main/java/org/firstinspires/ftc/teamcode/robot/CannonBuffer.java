package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.models.RobotModule;

import java.util.HashMap;

public class CannonBuffer implements RobotModule {
    private Telemetry globalTelemetry;
    private Servo servo;
    private boolean isRunning = false;

    public CannonBuffer(Telemetry globalTelemetry, Servo servo) {
        this.globalTelemetry = globalTelemetry;
        this.servo = servo;
    }

    @Override
    public void apply() {
        if (isRunning) {
            servo.setPosition(1.0);
        } else {
            servo.setPosition(0.5);
        }
        globalTelemetry.addData("CannonBuffer", "Current state: " + (isRunning ? "Running" : "Stopped"));
        globalTelemetry.update();
    }

    public void toggle() {
        if (isRunning) {
            servo.setPosition(0.5); // Stop
        } else {
            servo.setPosition(1.0); // Full speed forward
        }
        isRunning = !isRunning;
        globalTelemetry.addData("CannonBuffer", "Toggled: " + (isRunning ? "Running" : "Stopped"));
        globalTelemetry.update();
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("running", isRunning);
        return state;
    }

    @Override
    public void setState(HashMap<String, String> state) {
        if (state.containsKey("running")) {
            isRunning = Boolean.parseBoolean(state.get("running"));
            servo.setPosition(isRunning ? 1.0 : 0.5);
        }
    }
}
