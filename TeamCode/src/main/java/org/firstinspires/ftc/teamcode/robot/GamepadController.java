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

package org.firstinspires.ftc.teamcode.robot;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class GamepadController {
    /* CONFIG */
    public static double LONG_PRESS_TIME = 1.0;

    /* CLASS PROPERTIES */
    private final Telemetry globalTelemetry;
    private final Gamepad gamepad;
    private final ElapsedTime runtime;

    // I've not found any way to do this nicely, so it's just a separate variable for every button
    private boolean lastA = false;
    private boolean lastB = false;
    private boolean lastX = false;
    private boolean lastY = false;
    private boolean lastDPAD_UP = false;
    private boolean lastDPAD_DOWN = false;
    private boolean lastDPAD_LEFT = false;
    private boolean lastDPAD_RIGHT = false;
    private boolean lastLeftStick = false;
    private boolean lastRightStick = false;
    private boolean lastOptions = false;
    private boolean lastShare = false;
    private boolean lastHome = false;

    // Store the last time at which A was not pressed
    private double lastNotATime = Double.POSITIVE_INFINITY;
    private double lastNotBTime = Double.POSITIVE_INFINITY;
    private double lastNotXTime = Double.POSITIVE_INFINITY;
    private double lastNotYTime = Double.POSITIVE_INFINITY;

    /* CONSTRUCTOR */
    public GamepadController(Telemetry globalTelemetry, ElapsedTime globalRuntime, Gamepad globalGamepad) {
        // INITIALIZE TELEMETRY
        this.globalTelemetry = globalTelemetry;
        gamepad = globalGamepad;
        runtime = globalRuntime;
    }

    /* BUTTONS */
    // Get if a button is pressed, only returns true once
    public boolean press(Button button) {
        boolean pressed = false;
        switch (button) {
            case A:
                pressed = gamepad.a && !lastA;
                lastA = gamepad.a;
                break;

            case B:
                pressed = gamepad.b && !lastB;
                lastB = gamepad.b;
                break;

            case X:
                pressed = gamepad.x && !lastX;
                lastX = gamepad.x;
                break;

            case Y:
                pressed = gamepad.y && !lastY;
                lastY = gamepad.y;
                break;

            case DPAD_UP:
                pressed = gamepad.dpad_up && !lastDPAD_UP;
                lastDPAD_UP = gamepad.dpad_up;
                break;

            case DPAD_DOWN:
                pressed = gamepad.dpad_down && !lastDPAD_DOWN;
                lastDPAD_DOWN = gamepad.dpad_down;
                break;

            case DPAD_LEFT:
                pressed = gamepad.dpad_left && !lastDPAD_LEFT;
                lastDPAD_LEFT = gamepad.dpad_left;
                break;

            case DPAD_RIGHT:
                pressed = gamepad.dpad_right && !lastDPAD_RIGHT;
                lastDPAD_RIGHT = gamepad.dpad_right;
                break;

            case LEFT_STICK:
                pressed = gamepad.left_stick_button && !lastLeftStick;
                lastLeftStick = gamepad.left_stick_button;
                break;

            case RIGHT_STICK:
                pressed = gamepad.right_stick_button && !lastRightStick;
                lastRightStick = gamepad.right_stick_button;
                break;

            case OPTIONS:
                pressed = gamepad.options && !lastOptions;
                lastOptions = gamepad.options;
                break;

            case SHARE:
                pressed = gamepad.share && !lastShare;
                lastShare = gamepad.share;
                break;
            case HOME:
                pressed = gamepad.guide && !lastHome;
                lastHome = gamepad.guide;
                break;
        }

        return pressed;
    }

    // Get if a button is long-pressed, returns true while the button is not released (does not currently support left/right stick)
    public boolean longPress(Button button) {
        boolean longPressed = false;
        switch (button) {
            case A:
                if (!gamepad.a) lastNotATime = runtime.time();
                longPressed = gamepad.a && (runtime.time() - lastNotATime > LONG_PRESS_TIME);
                break;

            case B:
                if (!gamepad.b) lastNotBTime = runtime.time();
                longPressed = gamepad.b && (runtime.time() - lastNotBTime > LONG_PRESS_TIME);
                break;

            case X:
                if (!gamepad.x) lastNotXTime = runtime.time();
                longPressed = gamepad.x && (runtime.time() - lastNotXTime > LONG_PRESS_TIME);
                break;

            case Y:
                if (!gamepad.y) lastNotYTime = runtime.time();
                longPressed = gamepad.y && (runtime.time() - lastNotYTime > LONG_PRESS_TIME);
                break;
        }
        return longPressed;
    }

    public enum Button {
        X,
        Y,
        A,
        B,
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT,
        LEFT_STICK,
        UPSTICK,

        RIGHT_STICK,
        OPTIONS,
        SHARE,
        HOME
    }
}
