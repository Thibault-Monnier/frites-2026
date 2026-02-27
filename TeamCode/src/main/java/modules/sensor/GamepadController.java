package modules.sensor;

import static config.GamepadConfig.DEBOUNCE_TIME;
import static config.GamepadConfig.DOUBLE_PRESS_INTERVAL;
import static config.GamepadConfig.LONG_PRESS_TIME;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
public class GamepadController {
    public final Gamepad gamepad;
    private final ElapsedTime runtime;

    public GamepadController(ElapsedTime globalRuntime, Gamepad globalGamepad) {
        gamepad = globalGamepad;
        runtime = globalRuntime;
    }

    /**
     * Update the internal state of all buttons. This function MUST be called at EVERY loop cycle,
     * before any queries are made.
     */
    public void update() {
        for (Button button : Button.values()) {
            button.update(gamepad, runtime);
        }
    }

    /** Returns true if the button mapping is active based on its press type. */
    public boolean isPressActive(ButtonMapping mapping) {
        switch (mapping.pressType) {
            case SINGLE_PRESS:
                return isPressed(mapping.button);
            case RELEASE:
                return isReleased(mapping.button);
            case CONTINUOUS_PRESS:
                return isPressing(mapping.button);
            case LONG_PRESS:
                return isLongPressed(mapping.button);
            case DOUBLE_PRESS:
                return isDoublePressed(mapping.button);
            default:
                return false;
        }
    }

    /** Returns true if the button is currently being pressed. */
    public boolean isPressing(Button button) {
        return button.down;
    }

    /** Returns true if the button was pressed since last update. Returns true only once. */
    public boolean isPressed(Button button) {
        return button.pressed;
    }

    /** Returns true if the button was released since last update. Returns true only once. */
    public boolean isReleased(Button button) {
        return button.released;
    }

    /** Returns true if the button has been held down for LONG_PRESS_TIME seconds. */
    public boolean isLongPressed(Button button) {
        double elapsedMs = (runtime.milliseconds() - button.lastTimePressed);
        return isPressing(button) && elapsedMs >= LONG_PRESS_TIME;
    }

    /**
     * Returns true if the button was pressed twice within DOUBLE_PRESS_INTERVAL seconds. Returns
     * true only once.
     */
    public boolean isDoublePressed(Button button) {
        double intervalMs = (button.lastTimePressed - button.previousTimePressed);
        return isPressed(button) && intervalMs <= DOUBLE_PRESS_INTERVAL;
    }

    public void rumble(int i) {
        this.gamepad.rumble(i);
    }

    public void ledRed(int durationMs) {
        this.gamepad.setLedColor(255, 0, 0, durationMs);
    }

    public void ledGreen(int durationMs) {
        this.gamepad.setLedColor(0, 255, 0, durationMs);
    }

    public enum PressType {
        /// Returns true only on the first update after the button is pressed.
        SINGLE_PRESS,
        /// Returns true only on the first update after the button is released.
        RELEASE,
        /// Returns true on every update as long as the button is being held down.
        CONTINUOUS_PRESS,
        /// Returns true on every update as long as the button has been held down for at least
        /// LONG_PRESS_TIME
        LONG_PRESS,
        /// Returns true only on the first update after the button is pressed, if it has been
        /// pressed twice within DOUBLE_PRESS_INTERVAL seconds.
        DOUBLE_PRESS,
    }

    public enum Button {
        A(gamepad -> gamepad.a),
        B(gamepad -> gamepad.b),
        X(gamepad -> gamepad.x),
        Y(gamepad -> gamepad.y),
        DPAD_UP(gamepad -> gamepad.dpad_up),
        DPAD_DOWN(gamepad -> gamepad.dpad_down),
        DPAD_LEFT(gamepad -> gamepad.dpad_left),
        DPAD_RIGHT(gamepad -> gamepad.dpad_right),
        LEFT_STICK(gamepad -> gamepad.left_stick_button),
        RIGHT_STICK(gamepad -> gamepad.right_stick_button),
        OPTIONS(gamepad -> gamepad.options),
        SHARE(gamepad -> gamepad.share),
        HOME(gamepad -> gamepad.guide),
        TRIGGER_RIGHT(gamepad -> gamepad.right_trigger > 0.5),
        TRIGGER_LEFT(gamepad -> gamepad.left_trigger > 0.5),
        BUMPER_RIGHT(gamepad -> gamepad.right_bumper),
        BUMPER_LEFT(gamepad -> gamepad.left_bumper);

        private final java.util.function.Function<Gamepad, Boolean> accessor;

        private boolean pressed = false;
        private boolean released = false;

        private boolean down = false;

        private double debounceStartTime = -10000.0;
        private double lastTimePressed = -10000.0;
        private double previousTimePressed = -10000.0;

        Button(java.util.function.Function<Gamepad, Boolean> accessor) {
            this.accessor = accessor;
        }

        public boolean get(Gamepad gamepad) {
            return accessor.apply(gamepad);
        }

        /** Update internal state for this button */
        public void update(Gamepad gamepad, ElapsedTime runtime) {
            pressed = false;
            released = false;

            if (runtime.milliseconds() - debounceStartTime < DEBOUNCE_TIME) {
                // Debouncing, ignore this press
                return;
            }

            boolean lastDown = down;
            down = get(gamepad);

            if (lastDown && !down) {
                // Just released now
                released = true;
                debounceStartTime = runtime.milliseconds();
            }

            if (down && !lastDown) {
                // Just pressed now
                pressed = true;

                previousTimePressed = lastTimePressed;
                lastTimePressed = runtime.milliseconds();
                debounceStartTime = lastTimePressed;
            }
        }
    }

    public static class ButtonMapping {
        public final Button button;
        public final PressType pressType;

        public ButtonMapping(Button button, PressType pressType) {
            this.button = button;
            this.pressType = pressType;
        }
    }
}
