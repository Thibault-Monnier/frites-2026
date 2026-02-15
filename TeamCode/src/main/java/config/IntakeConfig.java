package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

@Config
@Configurable
public class IntakeConfig {
    public static double INTAKE_MOVING_SPEED = -1.0f;
    public static double INTAKE_STOPPED_SPEED = -0.05f;
    public static double SWITCHER_CENTER_POS = 0.49;
    public static double SWITCHER_OFFSET = 0.105;
}
