package modules.actuator;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import math.Distance;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class CannonCalibrator extends Cannon {
    private static final double SPEED_CHANGE_OFFSET = 100;

    private final Map<Distance, Double> savedCalibrationData = new HashMap<>();

    public CannonCalibrator(Telemetry globalTelemetry, DcMotorEx motorLeft, DcMotorEx motorRight) {
        super(globalTelemetry, motorLeft, motorRight);
    }

    @Override
    public void update(Distance target2dDistance) {
        throw new UnsupportedOperationException("Do not call update() on CannonCalibrator");
    }

    @Override
    protected double computeVelocity(Distance target2dDistance) {
        return motorTargetVelocity;
    }

    public void speedup() {
        motorTargetVelocity += SPEED_CHANGE_OFFSET;
    }

    public void slowdown() {
        motorTargetVelocity -= SPEED_CHANGE_OFFSET;
    }

    public void saveCurrentCalibrationData(Distance target2dDistance) {
        savedCalibrationData.put(target2dDistance, this.motorLeft.getVelocity());
    }

    public void printCalibrationData() {
        globalTelemetry.addLine("--- Saved Calibration Data ---");
        globalTelemetry.addData("Values", savedCalibrationData.toString());
    }
}
