package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.models.RobotModule;

import java.util.HashMap;

public class Cannon implements RobotModule {
    private static final double[][] DIST_TO_POWER = {
        {0, 0.0},
        {25, 0.2},
        {75, 0.4},
        {125, 0.6},
        {200, 0.8},
    };
    private static final double DEFAULT_DIST = 50;

    private final Telemetry globalTelemetry;

    private final DcMotor motor;
    private double motorTargetPower;

    private boolean isRunning = false;
    private double lastTarget2dDistance = DEFAULT_DIST;

    public Cannon(Telemetry globalTelemetry, DcMotor motor) {
        this.globalTelemetry = globalTelemetry;
        this.motor = motor;
    }

    @Override
    public void apply() {
        motor.setPower(motorTargetPower);
    }

    /// Toggle cannon motor on/off.
    public void toggle() {
        isRunning = !isRunning;
    }

    /// Update motor power by using last registered target distance
    public void update() {
        update(lastTarget2dDistance);
    }

    /// Update motor power using a value interpolated from target distance.
    public void update(double target2dDistance) {
        lastTarget2dDistance = target2dDistance;
        if (isRunning) {
            computePower(target2dDistance);
        } else {
            motorTargetPower = 0.0;
        }
    }

    private void computePower(double QRCodeDistance) {
        // Use Lagrange interpolation to find the motor power
        double power = 0.0;
        for (int i = 0; i < DIST_TO_POWER.length; i++)
            power += lagrangeInterpolation(QRCodeDistance, i);
        motorTargetPower = Math.clamp(power, 0.0, 1.0);
        globalTelemetry.addData("Cannon Motor Power", motorTargetPower);
    }

    private double lagrangeInterpolation(double x, int i) {
        double result = DIST_TO_POWER[i][1];
        for (int j = 0; j < DIST_TO_POWER.length; j++) {
            if (j != i) {
                result *= (x - DIST_TO_POWER[j][0]) / (DIST_TO_POWER[i][0] - DIST_TO_POWER[j][0]);
            }
        }
        return result;
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
