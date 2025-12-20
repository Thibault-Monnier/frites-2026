package modules.actuator;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import math.Distance;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

public class Cannon implements RobotActuatorModule {
    // Keep this for reference
//    private static final double[][] DIST_CM_TO_POWER = {
//        {0, 0.0},
//        {50, 0.40},
//        {80, 0.41},
//        {100, 0.45},
//        {140, 0.45},
//        {175, 0.50},
//        {200, 0.55},
//        {250, 0.8},
//        {350, 0.85}
//    };

    // WARNING: These are in DEGREES/SECOND!
    private static final double[][] DIST_CM_TO_VELOCITY = {
            {0, 0.0},
            {50, 14400.0},      // 0.40 power = 2400 RPM
            {80, 14760.0},      // 0.41 power = 2460 RPM
            {100, 16200.0},     // 0.45 power = 2700 RPM
            {140, 16200.0},     // 0.45 power = 2700 RPM
            {175, 18000.0},     // 0.50 power = 3000 RPM
            {200, 19800.0},     // 0.55 power = 3300 RPM
            {250, 28800.0},     // 0.80 power = 4800 RPM
            {350, 30600.0}      // 0.85 power = 5100 RPM
    };

    protected final Telemetry globalTelemetry;

    protected final DcMotorEx motorLeft;
    protected final DcMotorEx motorRight;

    protected double motorTargetVelocity;

    private boolean isRunning = false;

    private static final AngleUnit velocityAngleUnit = AngleUnit.DEGREES;

    public Cannon(Telemetry globalTelemetry, DcMotorEx motorLeft, DcMotorEx motorRight) {
        this.globalTelemetry = globalTelemetry;
        this.motorLeft = motorLeft;
        this.motorRight = motorRight;

        this.motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        // F = 32 * 3600 / maxVelocity
        double F = 32.0 * 3600.0 / 2800.0;  // ≈ 41.14
        // P = 0.1 * F
        double P = 0.1 * F;  // ≈ 4.11
        // I = 0.1 * P
        double I = 0.1 * P;  // ≈ 0.41
        // D = 0 (start with zero)
        double D = 0.0;

        PIDFCoefficients pidf = new PIDFCoefficients(P, I, D, F);
        this.motorLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        this.motorRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        this.motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.motorLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        this.motorRight.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void apply() {
        motorLeft.setVelocity(motorTargetVelocity, velocityAngleUnit);
        motorRight.setVelocity(motorTargetVelocity, velocityAngleUnit);
    }

    /// Toggle cannon motor on/off.
    public void toggle() {
        isRunning = !isRunning;
    }

    /// Update motor power using a value interpolated from target distance.
    public void update(Distance target2dDistance) {
        if (isRunning) {
            motorTargetVelocity = computeVelocity(target2dDistance);
        } else {
            motorTargetVelocity = 0.0;
        }
        globalTelemetry.addData("Cannon Motor Velocity/target", motorRight.getVelocity(velocityAngleUnit) + "/" + motorTargetVelocity);
    }

    protected double computeVelocity(Distance target2dDistance) {
        double x = target2dDistance.getValue(DistanceUnit.CM);

        // clamp below minimum
        if (x <= DIST_CM_TO_VELOCITY[0][0]) return DIST_CM_TO_VELOCITY[0][1];

        // clamp above maximum
        if (x >= DIST_CM_TO_VELOCITY[DIST_CM_TO_VELOCITY.length - 1][0])
            return DIST_CM_TO_VELOCITY[DIST_CM_TO_VELOCITY.length - 1][1];

        for (int i = 0; i < DIST_CM_TO_VELOCITY.length - 1; i++) {
            double x0 = DIST_CM_TO_VELOCITY[i][0];
            double y0 = DIST_CM_TO_VELOCITY[i][1];
            double x1 = DIST_CM_TO_VELOCITY[i + 1][0];
            double y1 = DIST_CM_TO_VELOCITY[i + 1][1];

            if (x >= x0 && x <= x1) {
                double t = (x - x0) / (x1 - x0);
                return y0 + t * (y1 - y0);
            }
        }

        // TODO: Return something better, but do not throw
        return DIST_CM_TO_VELOCITY[0][1];
//        throw new IllegalStateException("Unreachable code reached in computePower.");
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
