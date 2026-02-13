package logic.position;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.HardwareMap;

import logic.PlayingField;
import logic.Team;

import math.Pose2D;
import math.Position2D;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class RobotPosition {
    private static RobotPosition instance;

    private final Telemetry globalTelemetry;
    private final FtcDashboard dashboard = FtcDashboard.getInstance();

    private final LimelightHandler limelightHandler;
    private final OdometryHandler odometryHandler;

    private final Team color;

    private final KalmanFilter kalmanFilter;
    private Pose2D pose;

    public static RobotPosition getInstance(
            Telemetry globalTelemetry,
            HardwareMap hardwareMap,
            Team color,
            boolean forceNewInstance) {
        if (instance == null || forceNewInstance) {
            instance = new RobotPosition(globalTelemetry, hardwareMap, color);
        }
        return instance;
    }

    public static RobotPosition getInstance(
            Telemetry globalTelemetry, HardwareMap hardwareMap, Team color) {
        return getInstance(globalTelemetry, hardwareMap, color, false);
    }

    private RobotPosition(Telemetry globalTelemetry, HardwareMap hardwareMap, Team color) {
        this.globalTelemetry = globalTelemetry;
        this.color = color;

        pose = PlayingField.startPose(color);
        limelightHandler = new LimelightHandler(globalTelemetry, hardwareMap);
        odometryHandler = new OdometryHandler(hardwareMap, globalTelemetry, pose);

        limelightHandler.start();

        this.kalmanFilter = new KalmanFilter(pose);

        this.globalTelemetry.addData("Initialized RobotPosition", pose.toString());
        this.globalTelemetry.update();
    }

    /** Resets the robot pose to the starting position. */
    public void resetPose() {
        pose = PlayingField.startPose(color);
        odometryHandler.setPose(pose);
    }

    /**
     * Updates the robot pose. This MUST be called each step to ensure the information is
     * up-to-date.
     */
    public void updatePose() {
        Pose2D previousPose = pose;

        odometryHandler.update();

        if (limelightHandler.update()) pose = computePoseFromLimelight();
        else pose = computePoseFromOdometry();

        if (pose.hasNaN()) {
            globalTelemetry.addLine("Computed pose has NaN values, using previous pose");
            pose = previousPose;
            odometryHandler.setPose(pose);
        }

        globalTelemetry.addData("Computed pose", pose.toString());
        renderFieldOverlayInDashboard();
    }

    /** Computes the robot pose using the Limelight and the Kalman filter. */
    private Pose2D computePoseFromLimelight() {
        globalTelemetry.addLine("Using pose from Limelight");

        Pose2D cameraPose = limelightHandler.getLastKnownPose();

        Pose2D odometryPose = odometryHandler.getPose();
        Pose2D odometryVelocity = odometryPose.subtract(pose);

        return kalmanFilter.unite(cameraPose, odometryVelocity);
    }

    /** Computes the robot pose using only odometry. */
    private Pose2D computePoseFromOdometry() {
        globalTelemetry.addLine("Using pose from Odometry");

        return odometryHandler.getPose();
    }

    /// Gets the current robot pose as a Pose2D
    public Pose2D getPose() {
        return pose;
    }

    /// Gets the current robot position as a Position2D
    public Position2D getPosition() {
        return new Position2D(pose);
    }

    public LimelightHandler getLimelightHandler() {
        return limelightHandler;
    }

    private void renderFieldOverlayInDashboard() {
        TelemetryPacket packet = new TelemetryPacket();

        double heading = pose.getHeading(AngleUnit.RADIANS);

        double robotXInches = pose.getX(DistanceUnit.INCH);
        double robotYInches = pose.getY(DistanceUnit.INCH);

        double lineLength = 8;
        double endXInches = robotXInches + lineLength * Math.cos(heading);
        double endYInches = robotYInches + lineLength * Math.sin(heading);

        packet.fieldOverlay().setStroke("red").strokeCircle(robotXInches, robotYInches, 4);
        packet.fieldOverlay()
                .setStroke("green")
                .strokeLine(robotXInches, robotYInches, endXInches, endYInches);
        dashboard.sendTelemetryPacket(packet);
    }

    public enum StartPosition {
        NORMAL,
    }
}
