package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.robot.Constants;

import java.util.List;

public class CameraLocalizer {
    private final Telemetry telemetry;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    private final HardwareMap hardwareMap;

    private Limelight3A limelight;

    public CameraLocalizer(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
    }

    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, Constants.LIMELIGHT_CAMERA_ID);
        limelight.pipelineSwitch(0);
    }

    public void start() {
        limelight.start();
        dashboard.startCameraStream(limelight, 0);
    }

    public void stop() {
        limelight.stop();
        dashboard.stopCameraStream();
    }

    public void localize() {
        LLResult result = limelight.getLatestResult();

        TelemetryPacket packet = new TelemetryPacket();

        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();

            if (!tags.isEmpty()) {
                LLResultTypes.FiducialResult tag = tags.get(0); // Use the first detected tag for robot position
                Pose3D pose = tag.getRobotPoseFieldSpace();

                double robotX = pose.getPosition().x * 39.37; // X coordinate on the field in inches (or meters depending on setup)
                double robotY = pose.getPosition().y * 39.37; // Y coordinate on the field
                dashboard.getTelemetry().addData("unit", pose.getPosition().unit);
                double robotHeading = pose.getOrientation().getYaw(); // Orientation in radians

                // Draw robot on the dashboard field overlay
                packet.fieldOverlay()
                        .setStroke("red") // color of the robot
                        .strokeCircle(robotX, robotY, 4); // radius of 4 units (inches/meters)

                // Optional: draw heading line
                double lineLength = 8; // units
                double endX = robotX + lineLength * Math.cos(robotHeading);
                double endY = robotY + lineLength * Math.sin(robotHeading);
                packet.fieldOverlay()
                        .setStroke("green")
                        .strokeLine(robotX, robotY, endX, endY);

                // Telemetry for debugging
                telemetry.addLine("--- Camera Localization ---");
                telemetry.addData("Robot X", robotX);
                telemetry.addData("Robot Y", robotY);
                telemetry.addData("Heading (deg)", Math.toDegrees(robotHeading));

                // Also send data to dashboard telemetry
                dashboard.getTelemetry().addData("Robot X", robotX);
                dashboard.getTelemetry().addData("Robot Y", robotY);
                dashboard.getTelemetry().addData("Heading", Math.toDegrees(robotHeading));
            }
        } else {
            telemetry.addLine("Nothing detected");
            dashboard.getTelemetry().clear();
        }

        dashboard.sendTelemetryPacket(packet);
    }

}
