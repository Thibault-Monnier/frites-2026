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

    private static final FieldElement FIELD =
            new FieldElement(new Position2D(), FieldConfig.FIELD_WIDTH, FieldConfig.FIELD_DEPTH, 0);

    private static final FieldElement BLUE_GOAL =
            new FieldElement(
                    new Position2D(
                            DistanceUnit.INCH,
                            -FIELD.halfWidth() + FieldConfig.GOAL_WIDTH / 2,
                            -FIELD.halfDepth() + FieldConfig.GOAL_DEPTH / 2),
                    FieldConfig.GOAL_WIDTH,
                    FieldConfig.GOAL_DEPTH,
                    FieldConfig.GOAL_HEIGHT);
    private static final FieldElement RED_GOAL =
            new FieldElement(
                    new Position2D(
                            DistanceUnit.INCH,
                            -FIELD.halfWidth() + FieldConfig.GOAL_WIDTH / 2,
                            FIELD.halfDepth() - FieldConfig.GOAL_DEPTH / 2),
                    FieldConfig.GOAL_WIDTH,
                    FieldConfig.GOAL_DEPTH,
                    FieldConfig.GOAL_HEIGHT);

    public static Pose2D startPose(Team color) {
        return color.isBlue() ? FieldConfig.BLUE_START_POSE : FieldConfig.RED_START_POSE;
    }

    public static Position2D goalPos(Team color) {
        return color.isBlue() ? BLUE_GOAL.position : RED_GOAL.position;
    }

    public static Pose2D autoModeLeavePose(Team color) {
        return color.isBlue()
                ? FieldConfig.AUTO_MODE_LEAVE_POS_BLUE
                : FieldConfig.AUTO_MODE_LEAVE_POS_RED;
    }

    public static Pose2D autoModeShootPose(Team color) {
        return color.isBlue()
                ? FieldConfig.AUTO_MODE_SHOOT_POS_BLUE
                : FieldConfig.AUTO_MODE_SHOOT_POS_RED;
    }

    public static Pose2D artifactRowEntryPose(Team color, Artifact.Row row) {
        switch (row) {
            case FRONT:
                return color.isBlue()
                        ? FieldConfig.BLUE_ARTIFACT_FRONT_ROW_ENTRY_POSE
                        : FieldConfig.RED_ARTIFACT_FRONT_ROW_ENTRY_POSE;
            case MIDDLE:
                return color.isBlue()
                        ? FieldConfig.BLUE_ARTIFACT_MIDDLE_ROW_ENTRY_POSE
                        : FieldConfig.RED_ARTIFACT_MIDDLE_ROW_ENTRY_POSE;
            case BACK:
                return color.isBlue()
                        ? FieldConfig.BLUE_ARTIFACT_BACK_ROW_ENTRY_POSE
                        : FieldConfig.RED_ARTIFACT_BACK_ROW_ENTRY_POSE;
            default:
                throw new IllegalArgumentException("Invalid artifact row: " + row);
        }
    }

    /// Calculates the angle from the robot's current position to the center of the specified goal.
    /// @param robotPos The current position of the robot.
    /// @param color The color of the goal to target.
    /// @return The angle to the center of the specified goal.
    public static Angle angleToGoal(Position2D robotPos, Team color) {
        Position2D goalPos = goalPos(color);
        DistanceUnit unit = DistanceUnit.MM;
        double dx = goalPos.getX(unit) - robotPos.getX(unit);
        double dy = goalPos.getY(unit) - robotPos.getY(unit);
        return Angle.fromRadians(Math.atan2(dy, dx));
    }

    /// Calculates the distance from the robot's current position to the center of the specified
    /// goal.
    /// @param robotPos The current position of the robot.
    /// @param color The color of the goal to target.
    /// @return The distance to the center of the specified goal.
    public static Distance distanceToGoal(Position2D robotPos, Team color) {
        Position2D goalPos = goalPos(color);
        DistanceUnit unit = DistanceUnit.MM;
        double dist =
                Math.hypot(
                        goalPos.getX(unit) - robotPos.getX(unit),
                        goalPos.getY(unit) - robotPos.getY(unit));
        return new Distance(unit, dist);
    }

    /// Checks if a given position is within the boundaries of the playing field.
    /// @param pos The position to check.
    /// @return True if the position is within the field, false otherwise.
    public static boolean isInField(Position2D pos) {
        double x = pos.getX(DistanceUnit.INCH);
        double y = pos.getY(DistanceUnit.INCH);
        return x >= -FieldConfig.FIELD_WIDTH / 2
                && x <= FieldConfig.FIELD_WIDTH / 2
                && y >= -FieldConfig.FIELD_DEPTH / 2
                && y <= FieldConfig.FIELD_DEPTH / 2;
    }
}
