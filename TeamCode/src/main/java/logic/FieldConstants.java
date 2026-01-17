package logic;

import com.acmerobotics.dashboard.config.Config;

import math.Pose2D;

import modules.HardwareConstants;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class FieldConstants {
    /* --- Dimensions --- */
    public static final double FIELD_WIDTH = 144.0;
    public static final double FIELD_DEPTH = 144.0;
    public static final double TILE_WIDTH = FIELD_WIDTH / 6.0;
    public static final double TILE_DEPTH = FIELD_DEPTH / 6.0;

    public static final double GOAL_HEIGHT = 54.0;
    public static final double GOAL_WIDTH = 27.0;
    public static final double GOAL_DEPTH = 27.0;

    /* --- Starting positions --- */
    public static final Pose2D BLUE_START_POSE =
            new Pose2D(DistanceUnit.METER, -1.250, -1.370, AngleUnit.DEGREES, -138.0);
    public static final Pose2D RED_START_POSE =
            new Pose2D(DistanceUnit.METER, -1.215, 1.215, AngleUnit.DEGREES, 138.0);

    public static final Pose2D AUTO_MODE_SHOOT_POS_BLUE =
            new Pose2D(
                    DistanceUnit.INCH,
                    -TILE_WIDTH - TILE_WIDTH / 3.0,
                    -TILE_WIDTH,
                    AngleUnit.DEGREES,
                    -135);
    public static final Pose2D AUTO_MODE_SHOOT_POS_RED =
            new Pose2D(
                    DistanceUnit.INCH,
                    -TILE_WIDTH - TILE_WIDTH / 3.0,
                    TILE_WIDTH,
                    AngleUnit.DEGREES,
                    135);

    public static final Pose2D AUTO_MODE_LEAVE_POS_BLUE =
            new Pose2D(DistanceUnit.INCH, TILE_WIDTH / 2, -TILE_WIDTH, AngleUnit.DEGREES, -90);
    public static final Pose2D AUTO_MODE_LEAVE_POS_RED =
            new Pose2D(DistanceUnit.INCH, TILE_WIDTH / 2, TILE_WIDTH, AngleUnit.DEGREES, 90);

    // Front is the row closest to the audience, back is the row furthest from the audience, middle
    // is in between
    public static final Pose2D BLUE_ARTIFACT_FRONT_ROW_ENTRY_POSE =
            new Pose2D(DistanceUnit.INCH, TILE_WIDTH * 1.5 + 3.0, -32.0, AngleUnit.DEGREES, 270);
    public static final Pose2D BLUE_ARTIFACT_MIDDLE_ROW_ENTRY_POSE =
            new Pose2D(DistanceUnit.INCH, TILE_WIDTH * 0.5 + 3.0, -32.0, AngleUnit.DEGREES, 270);
    public static final Pose2D BLUE_ARTIFACT_BACK_ROW_ENTRY_POSE =
            new Pose2D(DistanceUnit.INCH, -TILE_WIDTH * 0.5, -32.0, AngleUnit.DEGREES, 270);

    // Front is the row closest to the audience, back is the row furthest from the audience, middle
    // is in between
    public static final Pose2D RED_ARTIFACT_FRONT_ROW_ENTRY_POSE =
            new Pose2D(DistanceUnit.INCH, TILE_WIDTH * 1.5 + 3.0, 32.0, AngleUnit.DEGREES, 90);
    public static final Pose2D RED_ARTIFACT_MIDDLE_ROW_ENTRY_POSE =
            new Pose2D(DistanceUnit.INCH, TILE_WIDTH * 0.5 + 3.0, 32.0, AngleUnit.DEGREES, 90);
    public static final Pose2D RED_ARTIFACT_BACK_ROW_ENTRY_POSE =
            new Pose2D(DistanceUnit.INCH, -TILE_WIDTH * 0.5, 32.0, AngleUnit.DEGREES, 90);
}
