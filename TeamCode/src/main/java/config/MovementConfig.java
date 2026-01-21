package config;

import com.acmerobotics.dashboard.config.Config;

@Config
public class MovementConfig {
    public static double FRONT_LEFT_COEFF = 1;
    public static double FRONT_RIGHT_COEFF = 1;
    public static double BACK_LEFT_COEFF = 1;
    public static double BACK_RIGHT_COEFF = 1;

    public static double SPEED_MULTIPLIER = 1.0;
    public static double SLOW_SPEED_MULTIPLIER = 0.5;
    public static double SUPER_SLOW_SPEED_MULTIPLIER = 0.2;
}
