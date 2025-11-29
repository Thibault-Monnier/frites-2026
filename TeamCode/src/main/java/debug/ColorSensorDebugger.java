package debug;

import android.graphics.Color;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import core.modules.Constants;

@Config
@TeleOp(
        name = Constants.DEBUGGER_MODES_GROUP + ": Color Sensor",
        group = Constants.DEBUGGER_MODES_GROUP)
public class ColorSensorDebugger extends OpMode {
    public static String sensorId = "color_sensor";

    private ColorSensor sensor;
    private Telemetry globalTelemetry;

    @Override
    public void init() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        sensor = hardwareMap.get(ColorSensor.class, sensorId);
        sensor.resetDeviceConfigurationForOpMode();
    }

    @Override
    public void loop() {
        globalTelemetry.addLine("--- Color Sensor Debugger --- ");
        globalTelemetry.addData("alpha", sensor.alpha());
        globalTelemetry.addData("argb", sensor.argb());
        globalTelemetry.addData("red", sensor.red());
        globalTelemetry.addData("green", sensor.green());
        globalTelemetry.addData("blue", sensor.blue());

        int r = sensor.red();
        int g = sensor.green();
        int b = sensor.blue();

        //            int rgb = Color.rgb(r, g, b);
        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);

        globalTelemetry.addLine("HSV:");
        globalTelemetry.addData("Hue", hsv[0]);
        globalTelemetry.addData("Saturation", hsv[1]);
        globalTelemetry.addData("Value", hsv[2]);
        globalTelemetry.update();
    }
}
