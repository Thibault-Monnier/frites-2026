package modules.actuator.cannon;

import static config.CannonConfig.CANNON_PID;
import static config.CannonConfig.ERROR_MARGIN;
import static config.CannonConfig.STABLE_THRESHOLD;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import config.HardwareConfig;

import logic.pidf.PIDFLControllerMotor;

import utils.Distance;

import modules.actuator.RobotActuatorModule;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

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

    private final PIDFLControllerMotor PIDFControllerLeft;
    private final PIDFLControllerMotor PIDFControllerRight;

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

        this.PIDFControllerLeft =
                new PIDFLControllerMotor(
                        motorLeft,
                        HardwareConfig.SHOOTER_MAX_VELOCITY,
                        this.globalTelemetry,
                        CANNON_PID);
        this.PIDFControllerRight =
                new PIDFLControllerMotor(
                        motorRight,
                        HardwareConfig.SHOOTER_MAX_VELOCITY,
                        this.globalTelemetry,
                        CANNON_PID);
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
        // 1.570 -> 1150
        // 2.390 -> 1250
        // 2.790 -> 1325
        // 3.950 -> 1500
        // Uses a cubic regression to interpolate between these values.

        double d = horizontalShootingDistance.getValue(DistanceUnit.METER);
        return -32.44301 * d * d * d + 272.71882 * d * d - 570.99292 * d + 1499.78524;
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
