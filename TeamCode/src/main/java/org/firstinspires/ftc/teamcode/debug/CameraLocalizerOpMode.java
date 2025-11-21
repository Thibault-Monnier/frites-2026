package org.firstinspires.ftc.teamcode.debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.field.LimelightHandler;

@TeleOp(name = "Camera Localizer OpMode", group = "Concept")
public class CameraLocalizerOpMode extends LinearOpMode {
    private Telemetry globalTelemetry;

    private LimelightHandler limelightHandler;

    public CameraLocalizerOpMode() {}

    @Override
    public void runOpMode() {
        initialize();

        waitForStart();

        limelightHandler.start();

        while (opModeIsActive()) {
            if (limelightHandler.update()) {
                Pose3D pose = limelightHandler.getLastKnownPose();
                Position pos = pose.getPosition();
                YawPitchRollAngles orient = pose.getOrientation();
                globalTelemetry.addLine("--- Last Known Pose: ---");
                globalTelemetry.addData(" Distance unit", pos.unit);
                globalTelemetry.addData("X", pos.x);
                globalTelemetry.addData("Y", pos.y);
                globalTelemetry.addData("Z", pos.z);
                globalTelemetry.addData("Roll (deg)", orient.getRoll(AngleUnit.DEGREES));
                globalTelemetry.addData("Pitch (deg)", orient.getPitch(AngleUnit.DEGREES));
                globalTelemetry.addData("Yaw (deg)", orient.getYaw(AngleUnit.DEGREES));
            }

            globalTelemetry.update();
        }

        limelightHandler.stop();
    }

    private void initialize() {
        globalTelemetry =
                new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        limelightHandler = new LimelightHandler(globalTelemetry, hardwareMap);
        limelightHandler.init();
    }
}
