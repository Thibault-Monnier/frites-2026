package logic;

import math.Pose2D;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class FieldConstants {
    /* --- Field dimensions --- */
    public static final double FIELD_WIDTH = 144.0;
    public static final double FIELD_DEPTH = 144.0;
    public static final double TILE_WIDTH = FIELD_WIDTH / 6.0;
    public static final double TILE_DEPTH = FIELD_DEPTH / 6.0;

    /* --- Starting positions --- */
    public static final Pose2D BLUE_START_POSE =
            new Pose2D(
                    DistanceUnit.INCH,
                    -TILE_WIDTH / 2,
                    -FIELD_WIDTH / 2 + TILE_DEPTH / 2,
                    AngleUnit.DEGREES,
                    90);
    public static final Pose2D RED_START_POSE =
            new Pose2D(
                    DistanceUnit.INCH,
                    TILE_WIDTH / 2,
                    -FIELD_WIDTH / 2 + TILE_DEPTH / 2,
                    AngleUnit.DEGREES,
                    90);

    /* --- Goal constants --- */
    public static final double GOAL_HEIGHT = 54.0;
    public static final double GOAL_WIDTH = 27.0;
    public static final double GOAL_DEPTH = 27.0;
}
