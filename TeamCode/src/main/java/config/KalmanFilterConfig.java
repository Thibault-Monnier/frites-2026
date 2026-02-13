package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

import math.Angle;
import math.Distance;

@Config
@Configurable
public class KalmanFilterConfig {
    public static Distance MODEL_VARIANCE_DIST = Distance.fromMillimeters(10);
    public static Angle MODEL_VARIANCE_ANGLE = Angle.fromDegrees(5);

    public static Distance CAMERA_VARIANCE_DIST = Distance.fromMillimeters(50);
    public static Angle CAMERA_VARIANCE_ANGLE = Angle.fromDegrees(15);
}
