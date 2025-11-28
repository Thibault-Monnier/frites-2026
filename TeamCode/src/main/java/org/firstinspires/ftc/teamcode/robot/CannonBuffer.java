package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.models.RobotModule;

import java.util.HashMap;

public class CannonBuffer implements RobotModule {
    private Telemetry globalTelemetry;
    private CRServo servo;
    private boolean isRunning = false;

    public CannonBuffer(Telemetry globalTelemetry, CRServo servo) {
        this.globalTelemetry = globalTelemetry;
        this.servo = servo;

        servo.setDirection(DcMotorSimple.Direction.REVERSE);
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
