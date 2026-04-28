package logic.position;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;

import logic.Team;
import logic.field.PlayingField;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import utils.TelemetryHandler;
import utils.TimeHelpers;
import utils.geometry.Angle;
import utils.geometry.Pose2D;
import utils.geometry.Position2D;
import utils.geometry.Transform2D;
import utils.geometry.Vector2D;
import utils.geometry.Velocity2D;

public class RobotPosition {
    private static RobotPosition instance;

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
            HardwareMap hardwareMap,
            Team color,
            boolean useFarStartPose,
            boolean forceNewInstance) {
        if (instance == null || forceNewInstance) {
            instance = new RobotPosition(hardwareMap, color, useFarStartPose);
        }
        return instance;
    }

    private RobotPosition(HardwareMap hardwareMap, Team color, boolean useFarStartPose) {
        this.color = color;

        if (useFarStartPose) pose = PlayingField.farStartPose(color);
        else pose = PlayingField.startPose(color);

        limelightHandler = new LimelightHandler(hardwareMap);
        odometryHandler = new OdometryHandler(hardwareMap, pose);

        limelightHandler.start();

        this.kalmanFilter = new KalmanFilter(pose);

        TelemetryHandler.addData("Initialized RobotPosition", pose.toString());
        TelemetryHandler.update();
    }

    public void start() {
        limelightHandler.start();

        odometryHandler.setPose(pose);
        odometryHandler.update();
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
            TelemetryHandler.addLine("Computed pose has NaN values, using previous pose");
            odometryPose = previousPose;
        }
        Transform2D odometryDelta = odometryPose.subtract(pose);

        pose = kalmanFilter.predict(odometryDelta);
        poseTimeSec = TimeHelpers.getRuntime();

        if (limelightHasNew) {
            TelemetryHandler.addLine("Using pose from Limelight with Kalman filter");
            Pose2D limelightPose = limelightHandler.getLastKnownPose();
            pose = kalmanFilter.update(limelightPose);
            odometryHandler.setPose(pose);
        } else TelemetryHandler.addLine("Using pose from Odometry");

        TelemetryHandler.addData("Odometry displacement", odometryDelta.toString());
        TelemetryHandler.addData("Computed pose", pose.toString());
        renderFieldOverlayInDashboard();
    }

    /// Gets the current robot pose as a Pose2D
    public Pose2D getPose() {
        return pose;
    }

    /// Gets the current robot position as a Position2D
    public Position2D getPosition() {
        return pose.getPosition();
    }

    /// Gets the current robot heading as an Angle
    public Angle getHeading() {
        return pose.getHeading();
    }

    /// Gets the current robot velocity
    public Velocity2D getPoseVelocity() {
        double time = poseTimeSec - previousPoseTimeSec;
        return new Velocity2D(previousPose, pose, time);
    }

    /// Gets the current robot velocity as a Pedropathing Pose, in displacement / second
    public Pose getPedroPoseVelocity() {
        Pose pedroPose = pose.toPedropathingPose();
        Pose previousPedroPose = previousPose.toPedropathingPose();

        Pose displacement = pedroPose.minus(previousPedroPose);
        double time = poseTimeSec - previousPoseTimeSec;
        return displacement.scale(1 / time);
    }

    /// Gets the current velocity of a point relative to the robot
    public Velocity2D getPointVelocity(Vector2D relativePosition) {
        Position2D previousPointPos = previousPose.addRelative(relativePosition);
        Position2D currentPointPos = pose.addRelative(relativePosition);

        double time = poseTimeSec - previousPoseTimeSec;
        return new Velocity2D(previousPointPos, currentPointPos, time);
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
