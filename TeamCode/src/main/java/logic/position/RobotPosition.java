package logic.position;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.HardwareMap;

import logic.Team;
import logic.field.PlayingField;

import math.Angle;
import math.Pose2D;
import math.Position2D;
import math.TimeHelpers;
import math.Vector2D;

import org.firstinspires.ftc.robotcore.external.Telemetry;
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
    private Pose2D previousPose;
    private double poseTimeSec;
    private double previousPoseTimeSec;

    public static RobotPosition getInstance(
            Telemetry globalTelemetry,
            HardwareMap hardwareMap,
            Team color,
            boolean useFarStartPose,
            boolean forceNewInstance) {
        if (instance == null || forceNewInstance) {
            instance = new RobotPosition(globalTelemetry, hardwareMap, color, useFarStartPose);
        }
        return instance;
    }

    private RobotPosition(
            Telemetry globalTelemetry,
            HardwareMap hardwareMap,
            Team color,
            boolean useFarStartPose) {
        this.globalTelemetry = globalTelemetry;
        this.color = color;

        if (useFarStartPose) pose = PlayingField.farStartPose(color);
        else pose = PlayingField.startPose(color);

        limelightHandler = new LimelightHandler(globalTelemetry, hardwareMap);
        odometryHandler = new OdometryHandler(hardwareMap, globalTelemetry, pose);

        limelightHandler.start();

        this.kalmanFilter = new KalmanFilter(pose);

        this.globalTelemetry.addData("Initialized RobotPosition", pose.toString());
        this.globalTelemetry.update();
    }

    public void start() {
        limelightHandler.start();
    }

    public void stop() {
        limelightHandler.stop();
    }

    /** Resets the robot pose to the starting position. */
    public void resetPose() {
        pose = PlayingField.startPose(color);
        previousPose = pose;
        odometryHandler.setPose(pose);
    }

    /**
     * Updates the robot pose. This MUST be called each step to ensure the information is
     * up-to-date.
     */
    public void updatePose() {
        previousPose = pose;
        previousPoseTimeSec = poseTimeSec;

        odometryHandler.update();
        boolean limelightHasNew = limelightHandler.update();

        Pose2D odometryPose = odometryHandler.getPose();
        if (odometryPose.hasNaN()) {
            globalTelemetry.addLine("Computed pose has NaN values, using previous pose");
            odometryPose = previousPose;
        }
        Pose2D odometryVelocity = odometryPose.subtract(pose);

        pose = kalmanFilter.predict(odometryVelocity);
        poseTimeSec = TimeHelpers.getRuntime();

        if (limelightHasNew) {
            globalTelemetry.addLine("Using pose from Limelight with Kalman filter");
            Pose2D limelightPose = limelightHandler.getLastKnownPose();
            pose = kalmanFilter.update(limelightPose);
            odometryHandler.setPose(pose);
        } else globalTelemetry.addLine("Using pose from Odometry");

        globalTelemetry.addData("Odometry velocity", odometryVelocity.toString());
        globalTelemetry.addData("Computed pose", pose.toString());
        renderFieldOverlayInDashboard();
    }

    /// Gets the current robot pose as a Pose2D
    public Pose2D getPose() {
        return pose;
    }

    /// Gets the current robot position as a Position2D
    public Position2D getPosition() {
        return pose.toPosition2D();
    }

    /// Gets the current robot heading as an Angle
    public Angle getHeading() {
        return pose.getHeading();
    }

    /// Gets the current robot velocity as a Vector2D, in distance / second
    public Vector2D getVelocity() {
        Vector2D displacement = pose.subtract(previousPose).toPosition2D().toVector2D();
        double time = poseTimeSec - previousPoseTimeSec;
        return displacement.scale(1 / time);
    }

    /// Gets the current velocity of a point relative to the robot as a Vector2D, in distance /
    /// second
    public Vector2D getPointVelocity(Vector2D relativePosition) {
        Position2D previousPointPos = previousPose.addRelative(relativePosition);
        Position2D currentPointPos = pose.addRelative(relativePosition);

        Vector2D displacement = currentPointPos.subtract(previousPointPos).toVector2D();
        double time = poseTimeSec - previousPoseTimeSec;
        return displacement.scale(1 / time);
    }

    public LimelightHandler getLimelightHandler() {
        return limelightHandler;
    }

    private void renderFieldOverlayInDashboard() {
        TelemetryPacket packet = new TelemetryPacket();

        Angle heading = pose.getHeading();

        double robotXInches = pose.getX(DistanceUnit.INCH);
        double robotYInches = pose.getY(DistanceUnit.INCH);

        double lineLength = 8;
        double endXInches = robotXInches + lineLength * heading.cos();
        double endYInches = robotYInches + lineLength * heading.sin();

        packet.fieldOverlay().setStroke("red").strokeCircle(robotXInches, robotYInches, 4);
        packet.fieldOverlay()
                .setStroke("green")
                .strokeLine(robotXInches, robotYInches, endXInches, endYInches);
        dashboard.sendTelemetryPacket(packet);
    }
}
