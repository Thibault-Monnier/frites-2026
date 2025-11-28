package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.models.RobotModule;
import org.firstinspires.ftc.teamcode.utils.Distance;

import java.util.HashMap;

public class Cannon implements RobotModule {
    private static final double[][] DIST_CM_TO_POWER = {
        {0, 0.0},
        {25, 0.2},
        {75, 0.4},
        {125, 0.6},
        {200, 0.8},
    };

    private final Telemetry globalTelemetry;

    private final DcMotor motor;
    private double motorTargetPower;

    private boolean isRunning = false;

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

    /// Update motor power using a value interpolated from target distance.
    public void update(Distance target2dDistance) {
        if (isRunning) {
            motorTargetPower = computePower(target2dDistance);
        } else {
            motorTargetPower = 0.0;
        }
        globalTelemetry.addData("Cannon Motor Power", motorTargetPower);
    }

    private double computePower(Distance target2dDistance) {
        // Use Lagrange interpolation to find the motor power
        double power = 0.0;
        for (int i = 0; i < DIST_CM_TO_POWER.length; i++)
            power += lagrangeInterpolation(target2dDistance, i);
        return Math.clamp(power, 0.0, 1.0);
    }

    private double lagrangeInterpolation(Distance x, int i) {
        double result = DIST_CM_TO_POWER[i][1];
        for (int j = 0; j < DIST_CM_TO_POWER.length; j++) {
            if (j != i) {
                result *=
                        (x.getValue(DistanceUnit.CM) - DIST_CM_TO_POWER[j][0])
                                / (DIST_CM_TO_POWER[i][0] - DIST_CM_TO_POWER[j][0]);
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
