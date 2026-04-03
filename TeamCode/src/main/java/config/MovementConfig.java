package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

import logic.pidf.PIDFLCoefficients;

import utils.math.Angle;
import utils.math.Distance;

@Config
@Configurable
public class MovementConfig {
    public static double FRONT_LEFT_COEFF = 1;
    public static double FRONT_RIGHT_COEFF = 1;
    public static double BACK_LEFT_COEFF = 1;
    public static double BACK_RIGHT_COEFF = 1;

    public static double SPEED_MULTIPLIER = 1.0;
    public static double SLOW_SPEED_MULTIPLIER = 0.5;
    public static double SUPER_SLOW_SPEED_MULTIPLIER = 0.225;

    public static PIDFLCoefficients TURN_PIDF_COEFFICIENTS =
            new PIDFLCoefficients(1.0, 0.0, 0.05, 0.0, 0.1);
    public static Angle TURN_TOLERANCE = Angle.fromDegrees(5.0);
    public static Angle NOT_TURNING_THRESHOLD = Angle.fromDegrees(0.5); // per frame

    public static PIDFLCoefficients TRANSLATION_PIDF_COEFFICIENTS =
            new PIDFLCoefficients(0.0025, 0, 0.00045, 0.0, 0.1);
    public static Distance TRANSLATION_TOLERANCE = Distance.fromMillimeters(5);
    public static Distance NOT_TRANSLATING_THRESHOLD = Distance.fromMillimeters(1); // per frame
}
