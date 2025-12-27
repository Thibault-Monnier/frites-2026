package modules.actuator;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import math.Distance;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

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
    private static final double[][] DIST_CM_TO_VELOCITY = {
        {0, 0.0},
        {50, 14400.0},
        {80, 14760.0},
        {100, 16200.0},
        {140, 16200.0},
        {175, 18000.0},
        {200, 19800.0},
        {250, 28800.0},
        {350, 30600.0}
    };

    protected final Telemetry globalTelemetry;

    private final DcMotorEx motorLeft;
    private final DcMotorEx motorRight;

    protected double motorTargetVelocity;

    private boolean isRunning = false;

    private static final AngleUnit velocityAngleUnit = AngleUnit.DEGREES;

    public Cannon(Telemetry globalTelemetry, DcMotorEx motorLeft, DcMotorEx motorRight) {
        this.globalTelemetry = globalTelemetry;
        this.motorLeft = motorLeft;
        this.motorRight = motorRight;

        this.motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

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
        globalTelemetry.addData(
                "Cannon Motor velocity/target", getAverageVelocity() + "/" + motorTargetVelocity);
    }

    /**
     * @return Average velocity between the two motors
     */
    protected double getAverageVelocity() {
        return (motorLeft.getVelocity(velocityAngleUnit)
                        + motorRight.getVelocity(velocityAngleUnit))
                / 2;
    }

    private boolean velocitiesEqual(double velocity, double target) {
        double errorMargin = 100;
        return Math.abs(velocity - target) <= errorMargin;
    }

    public boolean isReadyToShoot() {
        return velocitiesEqual(motorLeft.getVelocity(), motorTargetVelocity)
                && velocitiesEqual(motorRight.getVelocity(), motorTargetVelocity);
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

        throw new IllegalStateException("Unreachable code reached in computePower.");
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
