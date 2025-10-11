package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;

public class PlayingField {
    public static final FieldElement FIELD =
            new FieldElement(
                    new Vector2d(0, 0), FieldConstants.FIELD_WIDTH, FieldConstants.FIELD_DEPTH, 0);

    public static final FieldElement BLUE_GOAL =
            new FieldElement(
                    new Vector2d(
                            -FIELD.halfWidth() + FieldConstants.GOAL_WIDTH / 2,
                            -FIELD.halfDepth() + FieldConstants.GOAL_DEPTH / 2),
                    FieldConstants.GOAL_WIDTH,
                    FieldConstants.GOAL_DEPTH,
                    FieldConstants.GOAL_HEIGHT);
    public static final FieldElement RED_GOAL =
            new FieldElement(
                    new Vector2d(
                            -FIELD.halfWidth() + FieldConstants.GOAL_WIDTH / 2,
                            FIELD.halfDepth() - FieldConstants.GOAL_DEPTH / 2),
                    FieldConstants.GOAL_WIDTH,
                    FieldConstants.GOAL_DEPTH,
                    FieldConstants.GOAL_HEIGHT);

    private PlayingField() {}

    public static Pose2d startPose(org.firstinspires.ftc.teamcode.robot.Constants.Team color) {
        return color.isBlue() ? FieldConstants.BLUE_START_POSE : FieldConstants.RED_START_POSE;
    }

    private static Vector2d goalPos(org.firstinspires.ftc.teamcode.robot.Constants.Team color) {
        return color.isBlue() ? BLUE_GOAL.position : RED_GOAL.position;
    }

    /// Calculates the angle from the robot's current position to the center of the specified goal.
    /// @param robotPos The current position of the robot.
    /// @param color The color of the goal to target.
    /// @return The angle in radians to the center of the specified goal.
    public static double angleToGoal(
            Vector2d robotPos, org.firstinspires.ftc.teamcode.robot.Constants.Team color) {
        Vector2d goalPos = goalPos(color);
        return Math.atan2(goalPos.x - robotPos.x, goalPos.y - robotPos.y);
    }

    /// Calculates the distance from the robot's current position to the center of the specified
    /// goal.
    /// @param robotPos The current position of the robot.
    /// @param color The color of the goal to target.
    /// @return The distance to the center of the specified goal.
    public double distanceToGoal(
            Vector2d robotPos, org.firstinspires.ftc.teamcode.robot.Constants.Team color) {
        Vector2d goalPos = goalPos(color);
        return Math.hypot(goalPos.x - robotPos.x, goalPos.y - robotPos.y);
    }
}
