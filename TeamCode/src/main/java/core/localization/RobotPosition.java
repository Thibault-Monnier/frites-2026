package core.localization;

import com.qualcomm.robotcore.hardware.HardwareMap;

import core.math.Position2D;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class RobotPosition {
    private static RobotPosition instance;

    private final Telemetry globalTelemetry;

    private final LimelightHandler limelightHandler;
    private final OdometryHandler odometryHandler;

    private Pose2D pose;

    public static RobotPosition getInstance(Telemetry globalTelemetry, HardwareMap hardwareMap) {
        if (instance == null) {
            instance = new RobotPosition(globalTelemetry, hardwareMap);
        }
        return instance;
    }

    private RobotPosition(Telemetry globalTelemetry, HardwareMap hardwareMap) {
        this.globalTelemetry = globalTelemetry;

        limelightHandler = LimelightHandler.getInstance(globalTelemetry, hardwareMap);
        odometryHandler = OdometryHandler.getInstance(hardwareMap);
    }

    /// Updates the robot pose
    public void updatePose() {
        odometryHandler.update();

        if (limelightHandler.update()) {
            pose = limelightHandler.getLastKnownPose();
        } else {
            pose = odometryHandler.getPose();
        }
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
