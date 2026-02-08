package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

@Config
@Configurable
public class GamepadConfig {
    public static double LONG_PRESS_TIME = 500;
    public static double DOUBLE_PRESS_INTERVAL = 300;
    public static double DEBOUNCE_TIME = 50;
}
