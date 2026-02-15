package logic.field;

import config.FieldConfig;

import logic.Team;

import math.Angle;
import math.Distance;
import math.Pose2D;
import math.Position2D;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class PlayingField {
    private PlayingField() {
        // Prevent instantiation
    }

    public static final FieldElement FIELD =
            new FieldElement(
                    new Position2D(),
                    FieldConfig.FIELD_SIZE,
                    FieldConfig.FIELD_SIZE,
                    new Distance());

    private static final FieldElement RED_GOAL =
            new FieldElement(
                    new Position2D(FIELD.halfWidth().negate(), FIELD.halfDepth()),
                    FieldConfig.GOAL_WIDTH,
                    FieldConfig.GOAL_DEPTH,
                    FieldConfig.GOAL_HEIGHT);

    /// Switches the color of a Pose2D by negating the y-coordinate and heading, effectively
    /// mirroring it across the x-axis.
    private static Pose2D switchColor(Pose2D pose) {
        return new Pose2D(pose.getX(), pose.getY().negate(), pose.getHeading().negate());
    }

    /// Computes the pose of a point based on the team color. If the team is red, it
    /// returns the original pose; if the team is blue, it returns the mirrored pose across the
    /// x-axis.
    private static Pose2D switchColor(Pose2D redPose, Team color) {
        return color.isRed() ? redPose : switchColor(redPose);
    }

    /// Switches the color of a Position2D by negating the y-coordinate, effectively mirroring it
    /// across the x-axis.
    private static Position2D switchColor(Position2D pos) {
        return new Position2D(pos.getX(), pos.getY().negate());
    }

    /// Computes the position of a point based on the team color. If the team is red, it returns the
    /// original position; if the team is blue, it returns the mirrored position across the x-axis.
    private static Position2D switchColor(Position2D redPos, Team color) {
        return color.isRed() ? redPos : switchColor(redPos);
    }

    public static Pose2D startPose(Team color) {
        return switchColor(FieldConfig.RED_START_POSE, color);
    }

    public static Position2D goalPos(Team color) {
        return switchColor(RED_GOAL.position, color);
    }

    public static Pose2D autoModeLeavePose(Team color) {
        return switchColor(FieldConfig.RED_AUTO_MODE_LEAVE_POSE, color);
    }

    public static Position2D shootingPosition(Team color) {
        return switchColor(FieldConfig.RED_SHOOT_POS, color);
    }

    public static Pose2D parkingPose(Team color) {
        return switchColor(FieldConfig.RED_PARKING_POSE, color);
    }

    public static Pose2D artifactRowEntryPose(Team color, Artifact.Row row) {
        switch (row) {
            case FRONT:
                return switchColor(FieldConfig.RED_ARTIFACT_FRONT_ROW_ENTRY_POSE, color);
            case MIDDLE:
                return switchColor(FieldConfig.RED_ARTIFACT_MIDDLE_ROW_ENTRY_POSE, color);
            case BACK:
                return switchColor(FieldConfig.RED_ARTIFACT_BACK_ROW_ENTRY_POSE, color);
            default:
                throw new IllegalArgumentException("Invalid artifact row: " + row);
        }
    }

    /// Calculates the angle from the robot's current position to the targeting point of the
    /// specified goal.
    /// @param robotPos The current position of the robot.
    /// @param color The color of the goal to target.
    /// @return The angle to the targeting point of the specified goal.
    public static Angle angleToGoal(Position2D robotPos, Team color) {
        return goalPos(color).subtract(robotPos).direction();
    }

    /// Calculates the distance from the robot's current position to the targeting point of the
    /// specified goal.
    /// @param robotPos The current position of the robot.
    /// @param color The color of the goal to target.
    /// @return The distance to the targeting point of the specified goal.
    public static Distance distanceToGoal(Position2D robotPos, Team color) {
        Position2D goalPos = goalPos(color);
        return robotPos.distanceTo(goalPos);
    }

    /// Checks if a given position is within the boundaries of the playing field.
    /// @param pos The position to check.
    /// @return True if the position is within the field, false otherwise.
    public static boolean isInField(Position2D pos) {
        double x = pos.getX(DistanceUnit.MM);
        double y = pos.getY(DistanceUnit.MM);
        double halfWidth = FIELD.halfWidth().toMillimeters();
        return x >= -halfWidth && x <= halfWidth && y >= -halfWidth && y <= halfWidth;
    }
}
