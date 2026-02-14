package logic;

import config.FieldConfig;

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

    private static Pose2D switchColor(Pose2D pose) {
        return new Pose2D(pose.getX(), pose.getY().negate(), pose.getHeading().negate());
    }

    private static Pose2D switchColor(Pose2D redPose, Team color) {
        return color.isRed() ? redPose : switchColor(redPose);
    }

    private static Position2D switchColor(Position2D pos) {
        return new Position2D(pos.getX(), pos.getY().negate());
    }

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
        return switchColor(FieldConfig.AUTO_MODE_LEAVE_POSE_RED, color);
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
