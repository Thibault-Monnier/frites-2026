package org.firstinspires.ftc.teamcode.debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.field.CameraLocalizer;

@TeleOp(name = "Camera Localizer OpMode", group = "Concept")
public class CameraLocalizerOpMode extends LinearOpMode {
    private Telemetry globalTelemetry;

    private CameraLocalizer cameraLocalizer;

    public CameraLocalizerOpMode() {}

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        cameraLocalizer.start();

        while (opModeIsActive()) {
            cameraLocalizer.localize();
            globalTelemetry.update();
        }

        cameraLocalizer.stop();
    }

    private void initialize() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        cameraLocalizer = new CameraLocalizer(telemetry, hardwareMap);
        cameraLocalizer.init();
    }
}
