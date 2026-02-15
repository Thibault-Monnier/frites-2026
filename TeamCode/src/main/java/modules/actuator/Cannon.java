package modules.actuator;

import static config.CannonConfig.CANNON_PID;
import static config.CannonConfig.ERROR_MARGIN;
import static config.CannonConfig.STABLE_THRESHOLD;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import config.HardwareConfig;

import logic.pidf.PIDFControllerMotor;

import math.Distance;

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

    private final PIDFControllerMotor PIDFControllerLeft;
    private final PIDFControllerMotor PIDFControllerRight;

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
                new PIDFControllerMotor(
                        motorLeft,
                        HardwareConfig.SHOOTER_MAX_VELOCITY,
                        this.globalTelemetry,
                        CANNON_PID);
        this.PIDFControllerRight =
                new PIDFControllerMotor(
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

    /**
     * @return Whether the cannon is ready to shoot (at target velocity and stable)
     */
    public boolean isReadyToShoot() {
        return PIDFControllerLeft.isStableAtTarget(ERROR_MARGIN, STABLE_THRESHOLD)
                && PIDFControllerRight.isStableAtTarget(ERROR_MARGIN, STABLE_THRESHOLD)
                && getAverageVelocity() >= 100; // Not stopped
    }

    protected double computeVelocity(Distance target2dDistance) {
        double d = target2dDistance.getValue(DistanceUnit.CM);
        return d * d * d / 190000.0 - d * d / 1520.0 + d * 0.1517789 + 1143.648337;
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
