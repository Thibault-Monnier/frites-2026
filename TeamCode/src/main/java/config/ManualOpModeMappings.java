package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

import modules.sensor.GamepadController.*;

@Config
@Configurable
/// Contains button mappings for manual opmode.
public class ManualOpModeMappings {
    // Movement
    public static ButtonMapping SLOW_MOVE =
            new ButtonMapping(Button.LEFT_STICK, PressType.CONTINUOUS_PRESS);
    public static ButtonMapping SLOW_TURN =
            new ButtonMapping(Button.RIGHT_STICK, PressType.CONTINUOUS_PRESS);
    public static ButtonMapping LOCK_MOVE =
            new ButtonMapping(Button.BUMPER_LEFT, PressType.CONTINUOUS_PRESS);

    // Intake and transfer
    public static ButtonMapping INTAKE_ON =
            new ButtonMapping(Button.TRIGGER_LEFT, PressType.CONTINUOUS_PRESS);
    public static ButtonMapping INTAKE_AND_TRANSFER_REVERSE =
            new ButtonMapping(Button.A, PressType.CONTINUOUS_PRESS);

    // Cannon and shooting
    public static ButtonMapping CANNON_ON_OFF_TOGGLE =
            new ButtonMapping(Button.X, PressType.SINGLE_PRESS);
    public static ButtonMapping SHOOT =
            new ButtonMapping(Button.TRIGGER_RIGHT, PressType.CONTINUOUS_PRESS);
    public static ButtonMapping FORCE_SHOOT =
            new ButtonMapping(Button.BUMPER_RIGHT, PressType.CONTINUOUS_PRESS);

    // Macros
    public static ButtonMapping MOVE_TO_SHOOTING_SPOT =
            new ButtonMapping(Button.DPAD_UP, PressType.SINGLE_PRESS);
    public static ButtonMapping MOVE_TO_PARKING_SPOT =
            new ButtonMapping(Button.DPAD_DOWN, PressType.SINGLE_PRESS);

    // Misc
    public static ButtonMapping SUPER_SLOW_MODE_TOGGLE =
            new ButtonMapping(Button.B, PressType.DOUBLE_PRESS);
    public static ButtonMapping RESET_ROBOT_POSE =
            new ButtonMapping(Button.B, PressType.LONG_PRESS);
}
