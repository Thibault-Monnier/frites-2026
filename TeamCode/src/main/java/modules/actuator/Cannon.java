package modules.actuator;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import math.Distance;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

@Config
public class Cannon implements RobotActuatorModule {
    /*
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
    */
    // MAX VEL = 2600

    protected final Telemetry globalTelemetry;

    private final DcMotorEx motorLeft;
    private final DcMotorEx motorRight;

    protected double motorTargetVelocity;

    private boolean isRunning = false;

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

        PIDFCoefficients pidf = new PIDFCoefficients(0.135, 0, 1.35, 13.5);
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
        if (isRunning) {
            motorTargetVelocity = computeVelocity(target2dDistance);
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
        double velocityLeft = motorLeft.getVelocity();
        double velocityRight = motorRight.getVelocity();
        globalTelemetry.addData("MotorLeft velocity", velocityLeft);
        globalTelemetry.addData("MotorRight velocity", velocityRight);

        globalTelemetry.addData(
                "Speed/demand ratio",
                0.5
                        * ((motorTargetVelocity / velocityLeft)
                                + (motorTargetVelocity / velocityRight)));
        return Math.abs(velocityLeft + velocityRight) / 2;
    }

    private boolean velocitiesEqual(double velocity, double target) {
        double errorMargin = 50;
        return Math.abs(velocity - target) <= errorMargin;
    }

    public boolean isReadyToShoot() {
        return velocitiesEqual(this.getAverageVelocity(), motorTargetVelocity)
                && this.getAverageVelocity() >= 100;
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

        // Polynomial that approximates ideal power
        return 736.2669 + 4.2335 * x - 0.0063 * x * x;
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
