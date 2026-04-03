package utils;

import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TelemetryHandler {
    @Nullable private static Telemetry telemetry;

    private TelemetryHandler() {
        // Prevent instantiation
    }

    public static void instantiate(Telemetry telemetry) {
        TelemetryHandler.telemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    public static void update() {
        telemetry().update();
    }

    public static void addData(String key, Object value) {
        telemetry().addData(key, value);
        System.out.println(key + ": " + value.toString());
    }

    public static void addData(String key, String format, Object... args) {
        telemetry().addData(key, String.format(format, args));
    }

    public static void addLine(String line) {
        telemetry().addLine(line);
        System.out.println(line);
    }

    public static void clear() {
        telemetry().clear();
    }

    private static Telemetry telemetry() {
        if (telemetry == null) {
            throw new IllegalStateException("TelemetryHandler has not been instantiated.");
        }
        return telemetry;
    }
}
