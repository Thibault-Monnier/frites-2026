package modules.actuator;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import math.Distance;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.HashMap;

public class Cannon implements RobotActuatorModule {
    private static final double[][] DIST_CM_TO_POWER = {
        {0, 0.0}, {50, 0.40}, {80, 0.41}, {100, 0.45}, {140, 0.45}, {175, 0.50}, {200, 0.55}, {250, 0.8}, {350, 0.85}
    };

    protected final Telemetry globalTelemetry;

    private final DcMotorEx motorLeft;
    private final DcMotorEx motorRight;

    protected double motorTargetPower;

    private boolean isRunning = false;

    public Cannon(Telemetry globalTelemetry, DcMotorEx motorLeft, DcMotorEx motorRight) {
        this.globalTelemetry = globalTelemetry;
        this.motorLeft = motorLeft;
        this.motorRight = motorRight;

        this.motorLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        this.motorRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        this.motorLeft.setDirection(DcMotorEx.Direction.REVERSE);
        this.motorRight.setDirection(DcMotorEx.Direction.FORWARD);
    }

    @Override
    public void apply() {
        this.motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        motorLeft.setPower(-motorTargetPower); // Motors are mirrored
//        motorRight.setPower(motorTargetPower);
//        motorLeft.setVelocity(-motorTargetPower);
//        motorRight.setVelocity(motorTargetPower);
        motorLeft.setVelocity(50);
        motorRight.setVelocity(50);
        globalTelemetry.addData("velocity", motorLeft.getVelocity());
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

    protected double computePower(Distance target2dDistance) {
        double x = target2dDistance.getValue(DistanceUnit.CM);

        // clamp below minimum
        if (x <= DIST_CM_TO_POWER[0][0]) return DIST_CM_TO_POWER[0][1];

        // clamp above maximum
        if (x >= DIST_CM_TO_POWER[DIST_CM_TO_POWER.length - 1][0])
            return DIST_CM_TO_POWER[DIST_CM_TO_POWER.length - 1][1];

        for (int i = 0; i < DIST_CM_TO_POWER.length - 1; i++) {
            double x0 = DIST_CM_TO_POWER[i][0];
            double y0 = DIST_CM_TO_POWER[i][1];
            double x1 = DIST_CM_TO_POWER[i + 1][0];
            double y1 = DIST_CM_TO_POWER[i + 1][1];

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
