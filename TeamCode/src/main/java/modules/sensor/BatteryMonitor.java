package modules.sensor;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import utils.TelemetryHandler;

public class BatteryMonitor {
    private final VoltageSensor batteryVoltageSensor;

    public BatteryMonitor(HardwareMap hardwareMap) {
        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();
    }

    public void log() {
        double voltage = batteryVoltageSensor.getVoltage();
        TelemetryHandler.addData("Battery Voltage", "%.2f V", voltage);
    }
}
