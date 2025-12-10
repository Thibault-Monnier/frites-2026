package core.localization;

import com.qualcomm.robotcore.hardware.HardwareMap;

import core.Constants;
import core.logic.PlayingField;
import core.math.Pose2D;
import core.math.Position2D;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotPosition {
    private static RobotPosition instance;
    private static boolean started = false;

    private final Telemetry globalTelemetry;

    private final LimelightHandler limelightHandler;
    private final OdometryHandler odometryHandler;

    private Pose2D pose;

    public static RobotPosition getInstance(
            Telemetry globalTelemetry, HardwareMap hardwareMap, Constants.Team color) {
        if (instance == null) {
            instance = new RobotPosition(globalTelemetry, hardwareMap, color);
        }
        return instance;
    }

    private RobotPosition(
            Telemetry globalTelemetry, HardwareMap hardwareMap, Constants.Team color) {
        this.globalTelemetry = globalTelemetry;

        pose = PlayingField.startPose(color);
        limelightHandler = new LimelightHandler(globalTelemetry, hardwareMap);
        odometryHandler = new OdometryHandler(hardwareMap, pose);

        limelightHandler.init();
    }

    /// Starts internal handlers if not already started (e.g. if another opmode used this).
    /// This should be called in the start phase of each opmode.
    public void maybeStart() {
        if (started) return;
        started = true;
        limelightHandler.start();
    }

    /// Updates the robot pose
    public void updatePose() {
        odometryHandler.update();

        if (limelightHandler.update()) {
            pose = limelightHandler.getLastKnownPose();
            odometryHandler.setPoseBase(pose);
        } else {
            pose = odometryHandler.getPose();
        }

        globalTelemetry.addData("Computed pose", pose.toString());
    }

    /// Gets the current robot pose as a Pose2D
    public Pose2D getPose() {
        return pose;
    }

    /// Gets the current robot position as a Position2D
    public Position2D getPosition() {
        return new Position2D(pose);
    }
}
