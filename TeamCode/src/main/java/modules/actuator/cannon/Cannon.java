package modules.actuator.cannon;

import static config.CannonConfig.CANNON_PID;
import static config.CannonConfig.ERROR_MARGIN;
import static config.CannonConfig.STABLE_THRESHOLD;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import config.HardwareConfig;

import logic.pidf.PIDFLControllerMotor;

import modules.actuator.RobotActuatorModule;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import utils.TelemetryHandler;
import utils.geometry.Distance;

import java.util.HashMap;

public class Cannon implements RobotActuatorModule {
    private final DcMotorEx motorLeft;
    private final DcMotorEx motorRight;

    private final PIDFLControllerMotor PIDFControllerLeft;
    private final PIDFLControllerMotor PIDFControllerRight;

    protected double motorTargetVelocity;

    private boolean isRunning = false;

    public Cannon(DcMotorEx motorLeft, DcMotorEx motorRight) {
        this.motorLeft = motorLeft;
        this.motorRight = motorRight;

        // Without encoders is important to prevent the library from adding an extra PID over ours
        this.motorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.motorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        this.motorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.motorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        this.motorLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        this.motorRight.setDirection(DcMotorSimple.Direction.REVERSE);

        this.PIDFControllerLeft =
                new PIDFLControllerMotor(
                        motorLeft, HardwareConfig.SHOOTER_MAX_VELOCITY, CANNON_PID);
        this.PIDFControllerRight =
                new PIDFLControllerMotor(
                        motorRight, HardwareConfig.SHOOTER_MAX_VELOCITY, CANNON_PID);
    }

    @Override
    public void apply() {
        motorLeft.setPower(PIDFControllerLeft.get(motorTargetVelocity));
        motorRight.setPower(PIDFControllerRight.get(motorTargetVelocity));

        TelemetryHandler.addData(
                "Cannon Motor velocity/target", getAverageVelocity() + "/" + motorTargetVelocity);
        TelemetryHandler.addData("Cannon MotorLeft velocity", motorLeft.getVelocity());
        TelemetryHandler.addData("Cannon MotorRight velocity", motorRight.getVelocity());
    }

    /// Toggle cannon motor on/off.
    public void toggle() {
        isRunning = !isRunning;
    }

    /// Turn cannon motor on.
    public void on() {
        isRunning = true;
    }

    /// Turn cannon motor off.
    public void off() {
        isRunning = false;
    }

    public void setTargetVelocity(double value) {
        motorTargetVelocity = value;
    }

    /// Update motor power using a value interpolated from target distance.
    public void update(Distance horizontalShootingDistance) {
        if (isRunning) {
            setTargetVelocity(computeVelocity(horizontalShootingDistance));
        } else {
            setTargetVelocity(0);
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

    /**
     * @return Whether the cannon is ready to shoot (at target velocity and stable)
     */
    public boolean isReadyToShoot() {
        return PIDFControllerLeft.isStableAtTarget(ERROR_MARGIN, STABLE_THRESHOLD)
                && PIDFControllerRight.isStableAtTarget(ERROR_MARGIN, STABLE_THRESHOLD)
                && getAverageVelocity() >= 100; // Not stopped
    }

    protected double computeVelocity(Distance horizontalShootingDistance) {
        // Calibrated from the following data in (shooting distance meters, motor velocity):
        // 1.345 -> 1240
        // 1.826 -> 1360
        // 2.640 -> 1490
        // 3.181 -> 1580
        // 3.886 -> 1710
        // Uses a quartic regression to interpolate between these values.

        double d = horizontalShootingDistance.getValue(DistanceUnit.METER);
        return -14.0845 * d * d * d * d
                + 167.08063 * d * d * d
                - 717.12718 * d * d
                + 1483.50937 * d
                + 181.54309;
    }

    @Override
    public HashMap<String, Object> getCurrentState() {
        HashMap<String, Object> state = new HashMap<>();
        state.put("motorTargetVelocity", motorTargetVelocity);
        state.put("isRunning", isRunning);
        return state;
    }

    @Override
    public void setState(HashMap<String, String> state) {
        throw new UnsupportedOperationException("Cannon module does not support state loading.");
    }
}
