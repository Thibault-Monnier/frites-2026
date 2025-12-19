package modules.sensor;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import math.Pose2D;
import modules.HardwareConstants;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

import java.util.List;
import java.util.stream.Collectors;

public class LimelightHandler {
    private final Telemetry globalTelemetry;
    private final FtcDashboard dashboard = FtcDashboard.getInstance();
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

    public LimelightHandler(Telemetry globalTelemetry, HardwareMap hardwareMap) {
        this.globalTelemetry = globalTelemetry;
        this.hardwareMap = hardwareMap;
    }

    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, HardwareConstants.LIMELIGHT_CAMERA_ID);
        limelight.pipelineSwitch(0);
    }

    public void start() {
        limelight.start();
        dashboard.startCameraStream(limelight, 5);
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

        if (result == null || !result.isValid()) {
            handleNoDetection();
            return false;
        }

        if (!isNewResult(result)) return false;

        List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();
        lastDetectedTags = tags;

        printDetectionInfo(result);

        // Filter to only the positioning tags
        if (tags.stream()
                .noneMatch(tag -> tag.getFiducialId() == 20 || tag.getFiducialId() == 24)) {
            handleNoDetection();
            return false;
        }

        if (lastResult == null || isStableResult(result)) {
            validFramesInRow++;
        } else {
            validFramesInRow = 0;
        }
        lastResult = result;

        if (validFramesInRow < 3) {
            // Result is unstable
            return false;
        }

        // Stable result
        Pose3D pose = lastResult.getBotpose();
        Position pos = pose.getPosition();
        double heading = pose.getOrientation().getYaw(AngleUnit.RADIANS);
        lastKnownPose = new Pose2D(pos.unit, pos.x, pos.y, AngleUnit.RADIANS, heading);
        return true;
    }

    private void printDetectionInfo(LLResult result) {
        Pose3D pose = result.getBotpose();
        Position pos = pose.getPosition();

        globalTelemetry.addLine("--- Detected Tags ---");
        globalTelemetry.addData("Number of tags", lastDetectedTags.size());
        globalTelemetry.addData(
                "Tag IDs",
                lastDetectedTags.stream()
                        .map(LLResultTypes.FiducialResult::getFiducialId)
                        .collect(Collectors.toList()));

        globalTelemetry.addLine("--- Camera Localization ---");
        globalTelemetry.addData("Unit", pos.unit);
        globalTelemetry.addData("Robot X", pos.x);
        globalTelemetry.addData("Robot Y", pos.y);
        globalTelemetry.addData("Heading (deg)", pose.getOrientation().getYaw(AngleUnit.DEGREES));
    }

    private void handleNoDetection() {
        lastResult = null;
        validFramesInRow = 0;

        globalTelemetry.addLine("Nothing detected");
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
