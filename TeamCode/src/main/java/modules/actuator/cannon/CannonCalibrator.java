package modules.actuator.cannon;

import static config.CannonConfig.CALIBRATION_SPEED_CHANGE_OFFSET;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import math.Distance;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class CannonCalibrator extends Cannon {
    private final Map<Distance, Double> savedCalibrationData = new HashMap<>();

    public CannonCalibrator(Telemetry globalTelemetry, DcMotorEx motorLeft, DcMotorEx motorRight) {
        super(globalTelemetry, motorLeft, motorRight);
    }

    @Override
    public void update(Distance target2dDistance) {
        throw new UnsupportedOperationException("Do not call update() on CannonCalibrator");
    }

    public void speedup() {
        motorTargetVelocity += CALIBRATION_SPEED_CHANGE_OFFSET;
    }

    public void fastSpeedup() {
        motorTargetVelocity += 5 * CALIBRATION_SPEED_CHANGE_OFFSET;
    }

    public void slowdown() {
        motorTargetVelocity -= CALIBRATION_SPEED_CHANGE_OFFSET;
    }

    public void fastSlowdown() {
        motorTargetVelocity -= 5 * CALIBRATION_SPEED_CHANGE_OFFSET;
    }

    public void saveCurrentCalibrationData(Distance target2dDistance) {
        savedCalibrationData.clear();
        savedCalibrationData.put(target2dDistance, getAverageVelocity());
    }

    public void printCalibrationData() {
        globalTelemetry.addLine("--- Saved Calibration Data ---");
        globalTelemetry.addData("Values", savedCalibrationData.toString());
    }
}
