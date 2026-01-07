package modules.actuator;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import math.Distance;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

@Config
public class Cannon implements RobotActuatorModule {
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

    // NOTE: These are in degrees/second
//    private static final double[][] DIST_CM_TO_VELOCITY = {
//        {0, 0.0},
//        {50, 14400.0},
//        {80, 14760.0},
//        {100, 16200.0},
//        {140, 16200.0},
//        {175, 18000.0},
//        {200, 19800.0},
//        {250, 28800.0},
//        {350, 30600.0}
//    };

    public static double[][] DIST_CM_TO_VELOCITY = {
            {0, 0.0},
            {50, 900},
            {80, 1000},
            {100, 1100},
            {140, 1150},
            {175, 1200},
            {200, 1375},
            {250, 1450},
            {350, 1500}
    };
    // MAX VEL = 2600

    protected final Telemetry globalTelemetry;

    private final DcMotorEx motorLeft;
    private final DcMotorEx motorRight;

    protected double motorTargetVelocity;

    private boolean isRunning = false;

    private static final AngleUnit velocityAngleUnit = AngleUnit.DEGREES;

    private int updateVelocityCounter = 0;
    private int getVelocityCounter = 0;

    private double lastVelocityLeft = 0;
    private double lastVelocityRight = 0;

    public Cannon(Telemetry globalTelemetry, DcMotorEx motorLeft, DcMotorEx motorRight) {
        this.globalTelemetry = globalTelemetry;
        this.motorLeft = motorLeft;
        this.motorRight = motorRight;

        this.motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // F = 32 * 3600 / maxVelocity
//        double F = 32.0 * 3600.0 / 2800.0;  // ≈ 41.14
        // P = 0.1 * F
//        double P = 0.1 * F;  // ≈ 4.11
        // I = 0.1 * P
//        double I = 0.1 * P;  // ≈ 0.41
        // D = 0 (start with zero)
//        double D = 0.0;

        // maxV = 2600
        // F = 32767 / maxV = 32767/2600

        // P = 0.1 * F = 32767/26000
        // I = 0.1 * P = 32767/260000
        // D = 0

        PIDFCoefficients pidf = new PIDFCoefficients(0.135, 0,1.35, 13.5);
        this.motorLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        this.motorRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        this.motorLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        this.motorRight.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void apply() {
        motorLeft.setVelocity(motorTargetVelocity);
        motorRight.setVelocity(motorTargetVelocity);
    }

    /// Toggle cannon motor on/off.
    public void toggle() {
        isRunning = !isRunning;
    }

    /// Update motor power using a value interpolated from target distance.
    public void update(Distance target2dDistance) {
        getVelocityCounter++;
        updateVelocityCounter++;

        if (isRunning) {
            if (updateVelocityCounter >= 15) {
                motorTargetVelocity = computeVelocity(target2dDistance);
                updateVelocityCounter = 0;
            }
        } else {
            motorTargetVelocity = 0.0;
        }
        globalTelemetry.addData(
                "Cannon Motor velocity/target", getAverageVelocity() + "/" + motorTargetVelocity);
    }

    /**
     * @return Average velocity between the two motors
     */
    protected double getAverageVelocity() {
//        if (getVelocityCounter >= 15) {
            lastVelocityLeft = motorLeft.getVelocity();
            lastVelocityRight = motorRight.getVelocity();
            globalTelemetry.addData("MotorLeft velocity", lastVelocityLeft);
            globalTelemetry.addData("MotorRight velocity", lastVelocityRight);

            globalTelemetry.addData("Speed/demand ratio", 0.5 * ((motorTargetVelocity / lastVelocityLeft) + (motorTargetVelocity / lastVelocityRight)));
            getVelocityCounter = 0;
//        }
        return Math.abs(lastVelocityLeft
                    + lastVelocityRight)
                    * 0.5;
    }

    private boolean velocitiesEqual(double velocity, double target) {
//        double errorPercentage = (double) 0.15;
//        double errorMargin = velocity < 2000 ? 100 : velocity * errorPercentage;
        double errorMargin = 100;
        return velocity - 50 >= target && Math.abs(velocity - target) <= errorMargin;
    }

    public boolean isReadyToShoot() {
        return velocitiesEqual(this.getAverageVelocity(), motorTargetVelocity) && this.getAverageVelocity() >= 100;
    }

    protected double computeVelocity(Distance target2dDistance) {
        double x = target2dDistance.getValue(DistanceUnit.CM);

//        // clamp below minimum
//        if (x <= DIST_CM_TO_VELOCITY[0][0]) return DIST_CM_TO_VELOCITY[0][1];
//
//        // clamp above maximum
//        if (x >= DIST_CM_TO_VELOCITY[DIST_CM_TO_VELOCITY.length - 1][0])
//            return DIST_CM_TO_VELOCITY[DIST_CM_TO_VELOCITY.length - 1][1];
//
//        for (int i = 0; i < DIST_CM_TO_VELOCITY.length - 1; i++) {
//            double x0 = DIST_CM_TO_VELOCITY[i][0];
//            double y0 = DIST_CM_TO_VELOCITY[i][1];
//            double x1 = DIST_CM_TO_VELOCITY[i + 1][0];
//            double y1 = DIST_CM_TO_VELOCITY[i + 1][1];
//
//            if (x >= x0 && x <= x1) {
//                double t = (x - x0) / (x1 - x0);
//                return y0 + t * (y1 - y0);
//            }
//        }

//        return 812.5 + 2.525*x; // Fallback, should not reach here
        return 1 * (736.2669 + 4.2335*x - 0.0063 * x * x); // Fallback, should not reach here
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
