package modules.actuator.cannon;

import static config.CannonConfig.CALIBRATION_SPEED_CHANGE_OFFSET;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import utils.TelemetryHandler;
import utils.math.Distance;

import java.util.HashMap;
import java.util.Map;

public class CannonCalibrator extends Cannon {
    private final Map<Distance, Double> savedCalibrationData = new HashMap<>();

    public CannonCalibrator(DcMotorEx motorLeft, DcMotorEx motorRight) {
        super(motorLeft, motorRight);
    }

    @Override
    public void update(Distance horizontalShootingDistance) {}

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
        TelemetryHandler.addLine("--- Saved Calibration Data ---");
        TelemetryHandler.addData("Values", savedCalibrationData.toString());
    }
}
