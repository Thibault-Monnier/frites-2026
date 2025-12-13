package opmodes.debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import opmodes.GroupConstants;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
@TeleOp(
        name = GroupConstants.DEBUGGER_MODES_GROUP + ": Distance Sensor",
        group = GroupConstants.DEBUGGER_MODES_GROUP)
public class DistanceSensorDebugger extends OpMode {
    private static final String sensorId = "";
    private DistanceSensor distanceSensor;
    private Telemetry globalTelemetry;

    @Override
    public void init() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        distanceSensor = hardwareMap.get(DistanceSensor.class, sensorId);
        distanceSensor.resetDeviceConfigurationForOpMode();
    }

    @Override
    public void loop() {
        globalTelemetry.addLine("--- DEBUG INFOS ---");
        globalTelemetry.addData("ConnectionInfo", distanceSensor.getConnectionInfo());
        globalTelemetry.addData("DeviceName", distanceSensor.getDeviceName());
        globalTelemetry.addData("Manufacturer", distanceSensor.getManufacturer());
        globalTelemetry.addData("Version", distanceSensor.getVersion());

        // La ligne "réellement" intéressante
        globalTelemetry.addData("Distance (cm)", distanceSensor.getDistance(DistanceUnit.CM));
        globalTelemetry.update();
    }
}
