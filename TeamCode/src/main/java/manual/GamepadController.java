package manual;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
public class GamepadController {
    public static double LONG_PRESS_TIME = 0.5;
    public static double DOUBLE_PRESS_INTERVAL = 0.3;

    protected final Gamepad gamepad;
    protected final ElapsedTime runtime;

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

    /** Returns true if the button was pressed since last update. Returns true only once. */
    public boolean isPressed(Button button) {
        return isPressing(button) && !button.lastPressed;
    }

    /** Returns true if the button is currently being pressed. */
    public boolean isPressing(Button button) {
        return button.get(gamepad);
    }

    /** Returns true if the button has been held down for LONG_PRESS_TIME seconds. */
    public boolean isLongPressed(Button button) {
        double elapsedSeconds = (runtime.milliseconds() - button.lastTimePressed) / 1000.0;
        return isPressing(button) && elapsedSeconds >= LONG_PRESS_TIME;
    }

    /**
     * Returns true if the button was pressed twice within DOUBLE_PRESS_INTERVAL seconds. Returns
     * true only once.
     */
    public boolean isDoublePressed(Button button) {
        double intervalSeconds = (button.lastTimePressed - button.previousTimePressed) / 1000.0;
        return isPressed(button) && intervalSeconds <= DOUBLE_PRESS_INTERVAL;
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
        HOME(gamepad -> gamepad.guide);

        private final java.util.function.Function<Gamepad, Boolean> accessor;

        private boolean lastPressed = false;
        private boolean currentPressed = false;
        private double previousTimePressed = -10000.0;
        private double lastTimePressed = -10000.0;

        Button(java.util.function.Function<Gamepad, Boolean> accessor) {
            this.accessor = accessor;
        }

        public boolean get(Gamepad gamepad) {
            return accessor.apply(gamepad);
        }

        /** Update internal state for this button */
        public void update(Gamepad gamepad, ElapsedTime runtime) {
            lastPressed = currentPressed;
            currentPressed = get(gamepad);
            if (currentPressed && !lastPressed) {
                // Just pressed now
                previousTimePressed = lastTimePressed;
                lastTimePressed = runtime.milliseconds();
            }
        }
    }
}
