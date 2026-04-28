package modules.actuator.cannon;

import static config.CannonConfig.CALIBRATION_SPEED_CHANGE_OFFSET;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import utils.TelemetryHandler;
import utils.geometry.Distance;

import java.util.HashMap;
import java.util.Map;

public class CannonCalibrator extends Cannon {
    private final Map<Distance, Double> savedCalibrationData = new HashMap<>();
    private Distance lastCalibratedDistance = null;

    public CannonCalibrator(DcMotorEx motorLeft, DcMotorEx motorRight) {
        super(motorLeft, motorRight);
    }

    @Override
    public void update(Distance horizontalShootingDistance) {}

    public void speedup() {
        motorTargetVelocity += CALIBRATION_SPEED_CHANGE_OFFSET;
    }

    public void fastSpeedup() {
        motorTargetVelocity += 2 * CALIBRATION_SPEED_CHANGE_OFFSET;
    }

    public void slowdown() {
        motorTargetVelocity -= CALIBRATION_SPEED_CHANGE_OFFSET;
    }

    public void fastSlowdown() {
        motorTargetVelocity -= 2 * CALIBRATION_SPEED_CHANGE_OFFSET;
    }

    public void saveCurrentCalibrationData(Distance target2dDistance) {
        lastCalibratedDistance = target2dDistance;
        savedCalibrationData.put(target2dDistance, getAverageVelocity());
    }

    public void clearLastCalibrationData() {
        if (lastCalibratedDistance != null) {
            savedCalibrationData.remove(lastCalibratedDistance);
            lastCalibratedDistance = null; // Reset tracker after clearing
        }
    }

    public void printCalibrationData() {
        TelemetryHandler.addLine("--- Saved Calibration Data ---");
        TelemetryHandler.addData("Values", savedCalibrationData.toString());
    }
}
