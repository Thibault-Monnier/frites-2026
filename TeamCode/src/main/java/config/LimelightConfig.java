package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

@Config
@Configurable
public class LimelightConfig {
    public static double STABILITY_THRESHOLD_METERS = 0.15;
    public static int FRAMES_IN_A_ROW_THRESHOLD = 4;
}
