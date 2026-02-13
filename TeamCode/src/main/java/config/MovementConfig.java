package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

import logic.pidf.PIDFCoefficients;

import math.Angle;
import math.Distance;

@Config
@Configurable
public class MovementConfig {
    public static double FRONT_LEFT_COEFF = 1;
    public static double FRONT_RIGHT_COEFF = 1;
    public static double BACK_LEFT_COEFF = 1;
    public static double BACK_RIGHT_COEFF = 1;

    public static double SPEED_MULTIPLIER = 1.0;
    public static double SLOW_SPEED_MULTIPLIER = 0.5;
    public static double SUPER_SLOW_SPEED_MULTIPLIER = 0.2;

    public static PIDFCoefficients TURN_PIDF_COEFFICIENTS =
            new PIDFCoefficients(1.5, 0.0, 0.1, 0.0);
    public static Angle TURN_TOLERANCE = Angle.fromDegrees(4.0);
    public static Angle NOT_TURNING_THRESHOLD = Angle.fromDegrees(0.5); // per frame

    public static PIDFCoefficients TRANSLATION_PIDF_COEFFICIENTS =
            new PIDFCoefficients(0.002, 0.0, 0.0003, 0.0);
    public static Distance TRANSLATION_TOLERANCE = Distance.fromCentimeters(2.0);
    public static Distance NOT_TRANSLATING_THRESHOLD = Distance.fromCentimeters(0.3); // per frame
}
