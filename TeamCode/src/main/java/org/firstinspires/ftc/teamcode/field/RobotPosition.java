package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

public class RobotPosition {
    private final Telemetry globalTelemetry;

    private final LimelightHandler limelightHandler;

    private Pose3D pose;

    public RobotPosition(Telemetry globalTelemetry, HardwareMap hardwareMap) {
        this.globalTelemetry = globalTelemetry;

        limelightHandler = new LimelightHandler(globalTelemetry, hardwareMap);
    }

    /// Updates the robot pose
    public void updatePose() {
        if (limelightHandler.update()) {
            pose = limelightHandler.getLastKnownPose();
        }
    }

    /// Gets the current robot pose
    public Pose3D getPose() {
        return pose;
    }

    /// Gets the current robot position
    public Position getPosition() {
        return pose.getPosition();
    }

    /// Gets the current robot 2D position.
    /// @return A Vector2d representing the robot's X and Y coordinates on the field.
    public Vector2d get2dPosition() {
        Position pos = getPosition();
        return new Vector2d(pos.x, pos.y);
    }
}
