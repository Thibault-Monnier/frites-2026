package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

import math.Distance;

import modules.sensor.GoBildaPinpointDriver;

@Config
@Configurable
public class OdometryConfig {
    public static Distance ENCODER_X_Y_OFFSET = Distance.fromMillimeters(94);
    public static Distance ENCODER_Y_X_OFFSET = Distance.fromMillimeters(-155);

    public static GoBildaPinpointDriver.EncoderDirection ENCODER_X_DIRECTION =
            GoBildaPinpointDriver.EncoderDirection.REVERSED;
    public static GoBildaPinpointDriver.EncoderDirection ENCODER_Y_DIRECTION =
            GoBildaPinpointDriver.EncoderDirection.REVERSED;
}
