package modules.actuator;

import com.qualcomm.robotcore.hardware.DcMotor;

import math.Distance;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class CannonCalibrator extends Cannon {
    private static final double SPEED_CHANGE_OFFSET = 0.05;

    private final Map<Distance, Double> savedCalibrationData = new HashMap<>();

    public CannonCalibrator(Telemetry globalTelemetry, DcMotor motorLeft, DcMotor motorRight) {
        super(globalTelemetry, motorLeft, motorRight);
    }

    @Override
    public void update(Distance target2dDistance) {
        throw new UnsupportedOperationException("Do not call update() on CannonCalibrator");
    }

    @Override
    protected double computePower(Distance target2dDistance) {
        return motorTargetPower;
    }

    public void speedup() {
        motorTargetPower += SPEED_CHANGE_OFFSET;
    }

    public void slowdown() {
        motorTargetPower -= SPEED_CHANGE_OFFSET;
    }

    public void saveCurrentCalibrationData(Distance target2dDistance) {
        savedCalibrationData.put(target2dDistance, motorTargetPower);
    }

    public void printCalibrationData() {
        globalTelemetry.addLine("--- Saved Calibration Data ---");
        globalTelemetry.addData("Values", savedCalibrationData.toString());
    }
}
