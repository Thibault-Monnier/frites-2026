package org.firstinspires.ftc.teamcode.robot;

import com.acmerobotics.dashboard.config.Config;

@Config
public class Constants {
    /* --- SYSTEM --- */
    public static final String TAG = "FRITES";

    /* --- MODE REGISTRATION GROUPS --- */
    public static final String MAIN_MODES_GROUP = "Main";
    public static final String DEBUGGER_MODES_GROUP = "Debuggers";
    public static final String TEST_MODES_GROUP = "Tests";

    /* --- ROBOT GLOBAL CONSTANTS --- */
    public static double ROBOT_WIDTH = 18.0; // inches
    public static double ROBOT_DEPTH = 18.0; // inches
    public static double ROBOT_HEIGHT = 18.0; // inches

    /* --- ROBOT MODULES --- */
    // Empty for now

    /* --- HARDWARE MAP --- */
    public static String FRONT_LEFT_MOTOR_ID = "front_left";
    public static String FRONT_RIGHT_MOTOR_ID = "front_right";
    public static String BACK_LEFT_MOTOR_ID = "back_left";
    public static String BACK_RIGHT_MOTOR_ID = "back_right";
    public static String CANNON_MOTOR_1_ID = "cannon_motor_1";
    public static String CANNON_MOTOR_2_ID = "cannon_motor_2";

    /* --- TEST HARDWARE --- */
    public static String TEST_MOTOR_1_ID = "test_motor_1";
    public static String TEST_MOTOR_2_ID = "test_motor_2";
    public static double TEST_MOTOR_1_POWER = -1.0;
    public static double TEST_MOTOR_2_POWER = 1.0;

    public static String IMU_ID = "imu";

    /* --- UTILS --- */
    public static String REPLAY_FILE_DEST = "replay_file.txt";

    public enum Team {
        BLUE,
        RED,
        ANY_OR_UNKNOWN;

        public boolean isBlue() {
            return this == BLUE;
        }
    }

    public enum StartPosition {
        NORMAL,
    }

    public enum ColorRange {
        YELLOW(60, 80),
        RED(20, 40),
        BLUE(180, 200),
        UNKNOW(-1, -1);

        final int lowerBound;
        final int upperBound;

        ColorRange(int lowerBound, int upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        boolean valueInRange(int value) {
            return value >= lowerBound && value <= upperBound;
        }
    }
}
