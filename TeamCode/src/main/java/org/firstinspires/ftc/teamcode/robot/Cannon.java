package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.models.RobotModule;

public class Cannon implements RobotModule {
    public final String servoTargetPositionKey = "CannonTargetPosition";

    private final Telemetry globalTelemetry;

    private final Servo servo;

    private final double[][] distanceCmToPosition = {
        {0, 0.0},
        {25, 0.2},
        {50, 0.4},
        {75, 0.6},
        {100, 0.8}
    };

    private double servoTargetPosition;

    public Cannon(Telemetry globalTelemetry, Servo servo) {
        this.globalTelemetry = globalTelemetry;
        this.servo = servo;
        servoTargetPosition = 0.0;
    }

    @Override
    public void apply() {
        servo.setPosition(servoTargetPosition);
    }

    @Override
    public void setState(java.util.HashMap<String, String> state) {
        this.servoTargetPosition = Double.parseDouble(state.get(servoTargetPositionKey));
    }

    @Override
    public java.util.HashMap<String, Object> getCurrentState() {
        return new java.util.HashMap<String, Object>() {
            {
                put(servoTargetPositionKey, servoTargetPosition);
            }
        };
    }

    public void aim(double QRCodeDistanceCm) {
        // Use Lagrange interpolation to find the servo position
        double position = 0.0;
        for (int i = 0; i < distanceCmToPosition.length; i++)
            position += lagrangeInterpolation(QRCodeDistanceCm, i);
        servoTargetPosition = position;
        globalTelemetry.addData("Cannon Target Position", position);
    }

    private double lagrangeInterpolation(double x, int i) {
        double result = distanceCmToPosition[i][1];
        for (int j = 0; j < distanceCmToPosition.length; j++) {
            if (j != i) {
                result *=
                        (x - distanceCmToPosition[j][0])
                                / (distanceCmToPosition[i][0] - distanceCmToPosition[j][0]);
            }
        }
        return result;
    }
}
