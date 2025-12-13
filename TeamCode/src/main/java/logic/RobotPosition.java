package logic;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.HardwareMap;

import math.Pose2D;
import math.Position2D;
import modules.sensor.LimelightHandler;
import modules.sensor.OdometryHandler;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class RobotPosition {
    private static RobotPosition instance;
    private static boolean started = false;

    private final Telemetry globalTelemetry;
    private final FtcDashboard dashboard = FtcDashboard.getInstance();

    private final LimelightHandler limelightHandler;
    private final OdometryHandler odometryHandler;

    private Pose2D pose;

    public static RobotPosition getInstance(
            Telemetry globalTelemetry, HardwareMap hardwareMap, Team color) {
        if (instance == null) {
            instance = new RobotPosition(globalTelemetry, hardwareMap, color);
        }
        return instance;
    }

    private RobotPosition(Telemetry globalTelemetry, HardwareMap hardwareMap, Team color) {
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
            odometryHandler.setPose(pose);
        } else {
            pose = odometryHandler.getPose();
        }

        globalTelemetry.addData("Computed pose", pose.toString());
        renderFieldOverlayInDashboard();
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
