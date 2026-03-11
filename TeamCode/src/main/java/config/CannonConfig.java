package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

import logic.pidf.PIDFCoefficients;

import math.Angle;
import math.Distance;
import math.Vector2D;

@Config
@Configurable
public class CannonConfig {
    public static double MOVING_SPEED = 1.0;
    public static double REVERSE_SPEED = -0.65;

    public static PIDFCoefficients CANNON_PID = new PIDFCoefficients(0.02, 0.0, 0.0, -0.5);

    public static Angle CANNON_ANGLE = Angle.fromDegrees(61.0);
    public static Distance CANNON_TOP_HEIGHT = Distance.fromCentimeters(40.8);
    public static Vector2D CANNON_RELATIVE_POSITION =
            new Vector2D(Distance.fromCentimeters(-15.0), Distance.fromCentimeters(0.0));

    public static double SHOOT_DELAY = 0.375;
    public static int SHOOT_BALLS_AMOUNT = 4;

    public static double CALIBRATION_SPEED_CHANGE_OFFSET = 25.0;

    public static double ERROR_MARGIN = 40.0;
    public static double STABLE_THRESHOLD = 30.0;
}
