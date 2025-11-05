/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.manual;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
public class GamepadController {
    public static double LONG_PRESS_TIME = 1.0;
    public static double DOUBLE_PRESS_INTERVAL = 0.3;

    protected final Gamepad gamepad;
    protected final ElapsedTime runtime;

    public GamepadController(ElapsedTime globalRuntime, Gamepad globalGamepad) {
        // INITIALIZE TELEMETRY
        gamepad = globalGamepad;
        runtime = globalRuntime;
    }

    public void update() {
        for (Button button : Button.values()) {
            button.update(gamepad);
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
        private long previousTimePressed = 0;
        private long lastTimePressed = 0;

        Button(java.util.function.Function<Gamepad, Boolean> accessor) {
            this.accessor = accessor;
        }

        public boolean get(Gamepad gamepad) {
            return accessor.apply(gamepad);
        }

        /** Update internal state for this button */
        public void update(Gamepad gamepad) {
            boolean current = get(gamepad);
            if (current && !lastPressed) {
                // Just pressed now
                previousTimePressed = lastTimePressed;
                lastTimePressed = System.currentTimeMillis();
            }
            lastPressed = current;
        }
    }
}
