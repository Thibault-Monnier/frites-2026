package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

import math.Distance;

@Config
@Configurable
public class OdometryConfig {
    public static Distance ENCODER_X_Y_OFFSET = Distance.fromMillimeters(-95);
    public static Distance ENCODER_Y_X_OFFSET = Distance.fromMillimeters(-200);
}
