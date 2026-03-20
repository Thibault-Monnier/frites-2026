package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

import math.Angle;
import math.Distance;
import math.Pose2D;
import math.Position2D;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
@Configurable
public class FieldConfig {
    /* --- Dimensions --- */
    public static Distance FIELD_SIZE = Distance.fromInches(144);
    public static Distance TILE_SIZE = FIELD_SIZE.divide(6);

    public static Distance GOAL_HEIGHT = Distance.fromInches(54);
    public static Distance GOAL_WIDTH = Distance.fromInches(27);
    public static Distance GOAL_DEPTH = Distance.fromInches(27);

    public static Pose2D RED_START_POSE =
            new Pose2D(DistanceUnit.METER, -1.270, 1.270, AngleUnit.DEGREES, 135.0);
    public static Pose2D RED_FAR_START_POSE =
            new Pose2D(DistanceUnit.METER, -1.600, 0.457, AngleUnit.DEGREES, 180.0);

    public static Pose2D RED_AUTO_MODE_LEAVE_POSE =
            new Pose2D(TILE_SIZE, TILE_SIZE, Angle.fromDegrees(90));

    public static Position2D RED_SHOOT_POS = new Position2D(TILE_SIZE.negate(), TILE_SIZE);
    public static Pose2D RED_PARKING_POSE =
            new Pose2D(
                    TILE_SIZE.multiply(2).subtract(HardwareConfig.ROBOT_SIZE.halve()),
                    TILE_SIZE.negate().subtract(HardwareConfig.ROBOT_SIZE.halve()),
                    Angle.fromDegrees(90));

    public static Pose2D RED_RAMP_APPROACH_POSE =
            new Pose2D(TILE_SIZE.multiply(0.35), TILE_SIZE.multiply(2.6), Angle.fromDegrees(125));
    public static Pose2D RED_RAMP_DEFENSE_POSE =
            new Pose2D(TILE_SIZE.multiply(0), TILE_SIZE.multiply(2.2), Angle.fromDegrees(90));

    // Front is the row closest to the audience, back is the row furthest from the audience
    public static Pose2D RED_ARTIFACT_BACK_ROW_ENTRY_POSE =
            new Pose2D(TILE_SIZE.multiply(-0.5), TILE_SIZE, Angle.fromDegrees(90));
    public static Pose2D RED_ARTIFACT_MIDDLE_ROW_ENTRY_POSE =
            new Pose2D(TILE_SIZE.multiply(0.5), TILE_SIZE, Angle.fromDegrees(90));
    public static Pose2D RED_ARTIFACT_FRONT_ROW_ENTRY_POSE =
            new Pose2D(TILE_SIZE.multiply(1.5), TILE_SIZE, Angle.fromDegrees(90));

    public static final Distance ARTIFACT_COLLECTION_DISTANCE = Distance.fromCentimeters(90);
}
