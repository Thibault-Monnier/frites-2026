package core.logic;

import core.Constants;
import core.math.Distance;
import core.math.Pose2D;
import core.math.Position2D;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class PlayingField {
    private static final FieldElement FIELD =
            new FieldElement(
                    new Position2D(), FieldConstants.FIELD_WIDTH, FieldConstants.FIELD_DEPTH, 0);

    private static final FieldElement BLUE_GOAL =
            new FieldElement(
                    new Position2D(
                            DistanceUnit.INCH,
                            -FIELD.halfWidth() + FieldConstants.GOAL_WIDTH / 2,
                            FIELD.halfDepth() - FieldConstants.GOAL_DEPTH / 2),
                    FieldConstants.GOAL_WIDTH,
                    FieldConstants.GOAL_DEPTH,
                    FieldConstants.GOAL_HEIGHT);
    private static final FieldElement RED_GOAL =
            new FieldElement(
                    new Position2D(
                            DistanceUnit.INCH,
                            FIELD.halfWidth() - FieldConstants.GOAL_WIDTH / 2,
                            FIELD.halfDepth() - FieldConstants.GOAL_DEPTH / 2),
                    FieldConstants.GOAL_WIDTH,
                    FieldConstants.GOAL_DEPTH,
                    FieldConstants.GOAL_HEIGHT);

    public PlayingField() {}

    public static Pose2D startPose(Constants.Team color) {
        return color.isBlue() ? FieldConstants.BLUE_START_POSE : FieldConstants.RED_START_POSE;
    }

    private static Position2D goalPos(Constants.Team color) {
        return color.isBlue() ? BLUE_GOAL.position : RED_GOAL.position;
    }

    /// Calculates the angle from the robot's current position to the center of the specified goal.
    /// @param robotPos The current position of the robot.
    /// @param color The color of the goal to target.
    /// @return The angle in radians to the center of the specified goal.
    public static double angleToGoal(Position2D robotPos, Constants.Team color) {
        Position2D goalPos = goalPos(color);
        DistanceUnit unit = DistanceUnit.MM;
        return Math.atan2(
                goalPos.getX(unit) - robotPos.getX(unit), goalPos.getY(unit) - robotPos.getY(unit));
    }

    /// Calculates the distance from the robot's current position to the center of the specified
    /// goal.
    /// @param robotPos The current position of the robot.
    /// @param color The color of the goal to target.
    /// @return The distance to the center of the specified goal.
    public Distance distanceToGoal(Position2D robotPos, Constants.Team color) {
        Position2D goalPos = goalPos(color);
        DistanceUnit unit = DistanceUnit.MM;
        double dist =
                Math.hypot(
                        goalPos.getX(unit) - robotPos.getX(unit),
                        goalPos.getY(unit) - robotPos.getY(unit));
        return new Distance(unit, dist);
    }
}
