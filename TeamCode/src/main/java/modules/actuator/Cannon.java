package modules.actuator;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import logic.PIDCoefficients;
import logic.PIDController;

import math.Distance;

import modules.HardwareConstants;

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

    protected final Telemetry globalTelemetry;

    private final DcMotorEx motorLeft;
    private final DcMotorEx motorRight;

    private final PIDController pidControllerLeft;
    private final PIDController pidControllerRight;

    protected double motorTargetVelocity;

    private boolean isRunning = false;

    public Cannon(Telemetry globalTelemetry, DcMotorEx motorLeft, DcMotorEx motorRight) {
        this.globalTelemetry = globalTelemetry;
        this.motorLeft = motorLeft;
        this.motorRight = motorRight;

        this.motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        this.motorLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        this.motorRight.setDirection(DcMotorSimple.Direction.REVERSE);

        PIDCoefficients pid = new PIDCoefficients(50.0, 0.0, 0.0);

        this.pidControllerLeft =
                new PIDController(
                        motorLeft, HardwareConstants.SHOOTER_MAX_VELOCITY, globalTelemetry, pid);
        this.pidControllerRight =
                new PIDController(
                        motorRight, HardwareConstants.SHOOTER_MAX_VELOCITY, globalTelemetry, pid);
    }

    @Override
    public void apply() {
        motorLeft.setPower(pidControllerLeft.get(motorTargetVelocity));
        motorRight.setPower(pidControllerRight.get(motorTargetVelocity));

        globalTelemetry.addData(
                "Cannon Motor velocity/target", getAverageVelocity() + "/" + motorTargetVelocity);
        globalTelemetry.addData("Cannon MotorLeft velocity", motorLeft.getVelocity());
        globalTelemetry.addData("Cannon MotorRight velocity", motorRight.getVelocity());
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
    }

    /**
     * @return Average velocity between the two motors
     */
    protected double getAverageVelocity() {
        double velocityLeft = motorLeft.getVelocity();
        double velocityRight = motorRight.getVelocity();
        return Math.abs(velocityLeft + velocityRight) / 2;
    }

    private boolean velocitiesEqual(double velocity, double target) {
        double errorMargin = 40;
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
