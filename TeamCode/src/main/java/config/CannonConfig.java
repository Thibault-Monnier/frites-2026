package config;

import com.acmerobotics.dashboard.config.Config;

import logic.PIDFCoefficients;

@Config
public class CannonConfig {
    public static double MOVING_SPEED = 1.0;

    public static double CANNON_MIN_POWER = -0.75;
    public static double CANNON_MAX_POWER = -0.75;

    public static PIDFCoefficients CANNON_PID = new PIDFCoefficients(0.02, 0.0, 0.0, -0.5);

    public static double SHOOT_DELAY = 0.5;

    public static double CALIBRATION_SPEED_CHANGE_OFFSET = 25.0;
}
