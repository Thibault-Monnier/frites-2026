package modules.actuator;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import logic.PIDFCoefficients;
import logic.PIDFController;

import math.Distance;

import modules.HardwareConstants;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

@Config
public class Cannon implements RobotActuatorModule {
    /*
    Measure points:
        1.353 -> 1180
        1.988 -> 1230
        2.547 -> 1260
        3.231 -> 1380
    */

    protected final Telemetry globalTelemetry;

    private final DcMotorEx motorLeft;
    private final DcMotorEx motorRight;

    private final PIDFController PIDFControllerLeft;
    private final PIDFController PIDFControllerRight;

    protected double motorTargetVelocity;

    private boolean isRunning = false;

    public Cannon(Telemetry globalTelemetry, DcMotorEx motorLeft, DcMotorEx motorRight) {
        this.globalTelemetry = globalTelemetry;
        this.motorLeft = motorLeft;
        this.motorRight = motorRight;

        // Without encoders is important to prevent the library from adding an extra PID over ours
        this.motorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.motorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        this.motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        this.motorLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        this.motorRight.setDirection(DcMotorSimple.Direction.REVERSE);

        PIDFCoefficients pid = new PIDFCoefficients(0.02, 0.0, 0.0, -0.5);

        this.PIDFControllerLeft =
                new PIDFController(
                        motorLeft, HardwareConstants.SHOOTER_MAX_VELOCITY, globalTelemetry, pid);
        this.PIDFControllerRight =
                new PIDFController(
                        motorRight, HardwareConstants.SHOOTER_MAX_VELOCITY, globalTelemetry, pid);
    }

    @Override
    public void apply() {
        motorLeft.setPower(PIDFControllerLeft.get(motorTargetVelocity));
        motorRight.setPower(PIDFControllerRight.get(motorTargetVelocity));

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
        return x * x * x / 190000.0 + x * x / 10000.0 + x / 8.0 + 1150.0;
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
