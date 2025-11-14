package org.firstinspires.ftc.teamcode.debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
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
            if (cameraLocalizer.update()) {
                Pose3D pose = cameraLocalizer.getLastKnownPose();
                Position pos = pose.getPosition();
                YawPitchRollAngles orient = pose.getOrientation();
                globalTelemetry.addLine("--- Last Known Pose: ---");
                globalTelemetry.addData("X (in)", pos.x);
                globalTelemetry.addData("Y (in)", pos.y);
                globalTelemetry.addData("Z (in)", pos.z);
                globalTelemetry.addData("Roll (deg)", Math.toDegrees(orient.getRoll()));
                globalTelemetry.addData("Pitch (deg)", Math.toDegrees(orient.getPitch()));
                globalTelemetry.addData("Yaw (deg)", Math.toDegrees(orient.getYaw()));
            }
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
