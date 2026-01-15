package logic;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import math.Pose2D;
import math.Position2D;
import math.Units;

import modules.sensor.LimelightHandler;
import modules.sensor.OdometryHandler;

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
    public PoseVelocity2d updatePose() {
        odometryHandler.update();

        if (limelightHandler.update()) {
            globalTelemetry.addLine("Using pose from Limelight");
            pose = limelightHandler.getLastKnownPose();
            odometryHandler.setPose(pose);
        } else {
            globalTelemetry.addLine("Using pose from Odometry");
            pose = odometryHandler.getPose();
        }

        globalTelemetry.addData("Computed pose", pose.toString());
        renderFieldOverlayInDashboard();

        Vector2d worldVelocity =
                new Vector2d(
                        Units.mmToInches(odometryHandler.driver.getVelX()),
                        Units.mmToInches(odometryHandler.driver.getVelY()));
        Vector2d robotVelocity =
                Rotation2d.fromDouble(-odometryHandler.driver.getHeading()).times(worldVelocity);
        return new PoseVelocity2d(robotVelocity, odometryHandler.driver.getHeadingVelocity());
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
