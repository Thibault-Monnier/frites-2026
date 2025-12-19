package modules.sensor;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BatteryMonitor {
    private final VoltageSensor batteryVoltageSensor;
    private final Telemetry globalTelemetry;

    public BatteryMonitor(HardwareMap hardwareMap, Telemetry globalTelemetry) {
        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();
        this.globalTelemetry = globalTelemetry;
    }

    public void log() {
        double voltage = batteryVoltageSensor.getVoltage();
        globalTelemetry.addData("Battery Voltage", "%.2f V", voltage);
    }
}
