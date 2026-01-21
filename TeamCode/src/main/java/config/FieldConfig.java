package config;

import com.acmerobotics.dashboard.config.Config;

import math.Pose2D;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class FieldConfig {
    /* --- Dimensions --- */
    public static double FIELD_WIDTH = 144.0;
    public static double FIELD_DEPTH = 144.0;
    public static double TILE_WIDTH = FIELD_WIDTH / 6.0;
    public static double TILE_DEPTH = FIELD_DEPTH / 6.0;

    public static double GOAL_HEIGHT = 54.0;
    public static double GOAL_WIDTH = 27.0;
    public static double GOAL_DEPTH = 27.0;

    /* --- Starting positions --- */
    public static Pose2D BLUE_START_POSE =
            new Pose2D(DistanceUnit.METER, -1.250, -1.370, AngleUnit.DEGREES, -138.0);
    public static Pose2D RED_START_POSE =
            new Pose2D(DistanceUnit.METER, -1.215, 1.215, AngleUnit.DEGREES, 138.0);

    public static Pose2D AUTO_MODE_SHOOT_POS_BLUE =
            new Pose2D(DistanceUnit.INCH, -TILE_WIDTH, -TILE_WIDTH, AngleUnit.DEGREES, -135);
    public static Pose2D AUTO_MODE_SHOOT_POS_RED =
            new Pose2D(DistanceUnit.INCH, -TILE_WIDTH, TILE_WIDTH, AngleUnit.DEGREES, 135);

    public static Pose2D AUTO_MODE_LEAVE_POS_BLUE =
            new Pose2D(DistanceUnit.INCH, TILE_WIDTH, -TILE_WIDTH, AngleUnit.DEGREES, -90);
    public static Pose2D AUTO_MODE_LEAVE_POS_RED =
            new Pose2D(DistanceUnit.INCH, TILE_WIDTH, TILE_WIDTH, AngleUnit.DEGREES, 90);

    // Front is the row closest to the audience, back is the row furthest from the audience, middle
    // is in between
    public static Pose2D BLUE_ARTIFACT_FRONT_ROW_ENTRY_POSE =
            new Pose2D(
                    DistanceUnit.INCH,
                    TILE_WIDTH * 1.5 + TILE_WIDTH / 2.0,
                    -32.0,
                    AngleUnit.DEGREES,
                    260);
    public static Pose2D BLUE_ARTIFACT_MIDDLE_ROW_ENTRY_POSE =
            new Pose2D(
                    DistanceUnit.INCH,
                    TILE_WIDTH * 0.5 + TILE_WIDTH / 2.0,
                    -54.0,
                    AngleUnit.DEGREES,
                    260);
    public static Pose2D BLUE_ARTIFACT_BACK_ROW_ENTRY_POSE =
            new Pose2D(
                    DistanceUnit.INCH,
                    -TILE_WIDTH * 0.5 + TILE_WIDTH / 2.0,
                    -32.0,
                    AngleUnit.DEGREES,
                    260);

    // Front is the row closest to the audience, back is the row furthest from the audience, middle
    // is in between
    public static Pose2D RED_ARTIFACT_FRONT_ROW_ENTRY_POSE =
            new Pose2D(
                    DistanceUnit.INCH,
                    TILE_WIDTH * 1.5 + TILE_WIDTH / 2.0,
                    54.0,
                    AngleUnit.DEGREES,
                    100);
    public static Pose2D RED_ARTIFACT_MIDDLE_ROW_ENTRY_POSE =
            new Pose2D(
                    DistanceUnit.INCH, TILE_WIDTH * 0.5 + TILE_WIDTH, 32.0, AngleUnit.DEGREES, 100);
    public static Pose2D RED_ARTIFACT_BACK_ROW_ENTRY_POSE =
            new Pose2D(
                    DistanceUnit.INCH,
                    -TILE_WIDTH * 0.5 + TILE_WIDTH / 2.0,
                    32.0,
                    AngleUnit.DEGREES,
                    100);
}
