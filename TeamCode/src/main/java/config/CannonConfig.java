package config;

import com.acmerobotics.dashboard.config.Config;

import logic.PIDFCoefficients;

@Config
public class CannonConfig {
    public static double MOVING_SPEED = 1.0;

    public static PIDFCoefficients CANNON_PID = new PIDFCoefficients(0.02, 0.0, 0.0, -0.5);

    public static double SHOOT_DELAY = 0.3;

    public static double CALIBRATION_SPEED_CHANGE_OFFSET = 25.0;

    public static double ERROR_MARGIN = 40.0;
    public static double STABLE_THRESHOLD = 30.0;
}
