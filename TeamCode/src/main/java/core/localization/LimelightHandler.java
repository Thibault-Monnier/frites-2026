package core.localization;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import core.Constants;
import core.math.Pose2D;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

import java.util.List;

public class LimelightHandler {
    private final Telemetry telemetry;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    private final HardwareMap hardwareMap;

    private Limelight3A limelight;
    private int validFramesInRow = 0;
    private LLResult lastResult = null;
    private final double STABILITY_THRESHOLD_METERS = 0.15;

    private Pose2D lastKnownPose = null;

    private List<LLResultTypes.FiducialResult> lastDetectedTags = null;

    public List<LLResultTypes.FiducialResult> getLastDetectedTags() {
        return lastDetectedTags;
    }

    public LimelightHandler(Telemetry telemetry, HardwareMap hardwareMap) {
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

    public Pose2D getLastKnownPose() {
        return lastKnownPose;
    }

    /// Retrieves the latest camera data and updates internal state.
    /// @return true if a new stable pose has been set, false otherwise.
    public boolean update() {
        LLResult result = limelight.getLatestResult();

        if (!isNewResult(result)) return false;

        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();
            lastDetectedTags = tags;
            tags.removeIf(
                    tag -> {
                        int id = tag.getFiducialId();
                        return id != 20 && id != 24;
                    });

            if (tags.isEmpty()) {
                handleNoDetection();
                return false;
            }

            Pose3D finalPose = result.getBotpose();
            Position finalPos = finalPose.getPosition();


            telemetry.addLine("--- Camera Localization ---");
            telemetry.addData("Unit", finalPos.unit);
            telemetry.addData("Robot X", finalPos.x);
            telemetry.addData("Robot Y", finalPos.y);
            telemetry.addData("Heading", finalPose.getOrientation().getYaw(AngleUnit.DEGREES));

            telemetry.addLine();
            telemetry.addData("# of tags", tags.size());
            for (LLResultTypes.FiducialResult tag : tags) {
                Pose3D pose = tag.getRobotPoseFieldSpace();
                telemetry.addData("ID", tag.getFiducialId());
                telemetry.addData("tx deg", tag.getTargetXDegrees());
                telemetry.addData("ty deg", tag.getTargetYDegrees());
                telemetry.addData("pose", pose.toString());
            }

            if (lastResult == null || isStableResult(result)) {
                validFramesInRow++;
            } else {
                validFramesInRow = 0;
            }
            lastResult = result;

            if (validFramesInRow >= 3) {
                // Stable result
                Pose3D pose = lastResult.getBotpose();
                Position pos = pose.getPosition();
                double heading = pose.getOrientation().getYaw(AngleUnit.RADIANS);
                lastKnownPose = new Pose2D(pos.unit, pos.x, pos.y, AngleUnit.RADIANS, heading);
                renderFieldOverlayInDashboard();

                return true;
            }

        } else {
            handleNoDetection();
        }

        return false;
    }

    private void handleNoDetection() {
        lastResult = null;
        validFramesInRow = 0;

        telemetry.addLine("Nothing detected");
    }

    private void renderFieldOverlayInDashboard() {
        TelemetryPacket packet = new TelemetryPacket();

        double heading = lastKnownPose.getHeading(AngleUnit.RADIANS);

        double robotXInches = lastKnownPose.getX(DistanceUnit.INCH);
        double robotYInches = lastKnownPose.getY(DistanceUnit.INCH);

        double lineLength = 8;
        double endXInches = robotXInches + lineLength * Math.cos(heading);
        double endYInches = robotYInches + lineLength * Math.sin(heading);

        packet.fieldOverlay().setStroke("red").strokeCircle(robotXInches, robotYInches, 4);
        packet.fieldOverlay()
                .setStroke("green")
                .strokeLine(robotXInches, robotYInches, endXInches, endYInches);
        dashboard.sendTelemetryPacket(packet);
    }

    private double distance(Position p1, Position p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        double dz = p1.z - p2.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private boolean isNewResult(LLResult newResult) {
        return lastResult == null || lastResult.getTimestamp() != newResult.getTimestamp();
    }

    private boolean isStableResult(LLResult newResult) {
        Position newPos = newResult.getBotpose().getPosition();
        Position prevPos = lastResult.getBotpose().getPosition();
        return distance(newPos, prevPos) < STABILITY_THRESHOLD_METERS;
    }
}
