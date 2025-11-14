package org.firstinspires.ftc.teamcode.field;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.robot.Constants;

import java.util.List;

public class CameraLocalizer {
    private final Telemetry telemetry;
    private final HardwareMap hardwareMap;

    private Limelight3A limelight;
    private int validFramesInRow = 0;
    private LLResult lastResult = null;
    private final double STABILITY_THRESHOLD_METERS = 0.1;

    private Pose3D lastKnownPose = null;

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
    }

    public void stop() {
        limelight.stop();
    }

    public Pose3D getLastKnownPose() {
        return lastKnownPose;
    }

    /// Retrieves the latest camera data and updates internal state.
    /// @return true if a new stable pose has been set, false otherwise.
    public boolean update() {
        LLResult result = limelight.getLatestResult();

        if (result != null) {
            if (result.isValid()) {
                telemetry.addLine("--- Camera Localization ---");

                List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();

                telemetry.addData("# of tags", tags.size());

                for (LLResultTypes.FiducialResult tag : tags) {
                    telemetry.addData("ID", tag.getFiducialId());
                    telemetry.addData("tx deg", tag.getTargetXDegrees());
                    telemetry.addData("ty deg", tag.getTargetYDegrees());
                    telemetry.addData("pose", tag.getRobotPoseFieldSpace().toString());
                }

                if (lastResult == null || isStableNewResult(result)) {
                    validFramesInRow++;
                } else {
                    validFramesInRow = 0;
                }
                lastResult = result;

                if (validFramesInRow >= 3) {
                    // Stable result
                    lastKnownPose = lastResult.getBotpose();
                    return true;
                }

            } else {
                lastResult = null;
                validFramesInRow = 0;

                telemetry.addLine("Nothing detected");
            }
        }

        return false;
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

    private boolean isStableNewResult(LLResult newResult) {
        Position newPos = newResult.getBotpose().getPosition();
        Position prevPos = lastResult.getBotpose().getPosition();
        boolean isStable = distance(newPos, prevPos) < STABILITY_THRESHOLD_METERS;
        return isNewResult(newResult) && isStable;
    }
}
