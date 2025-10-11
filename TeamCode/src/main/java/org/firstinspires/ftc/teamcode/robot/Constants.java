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
    public static double CLAMP_OPEN_POSITION = 0.75;
    public static double CLAMP_CLOSED_POSITION = 1.0;

    public static double PLASTIC_CLAMP_OPEN_POSITION = 0.6;
    public static double PLASTIC_CLAMP_CLOSED_POSITION = 0.1;
    public static String PLASTIC_CLAMP_ID = "plastic_clamp";

    public static double BASKET_OPEN_POSITION = 0.5;
    public static double BASKET_CLOSED_POSITION = 0.0;
    public static String BASKET_ID = "basket";

    /* --- HARDWARE MAP --- */
    public static String FRONT_LEFT_MOTOR_ID = "front_left";
    public static String FRONT_RIGHT_MOTOR_ID = "front_right";
    public static String BACK_LEFT_MOTOR_ID = "back_left";
    public static String BACK_RIGHT_MOTOR_ID = "back_right";
    public static String ARM_MOTOR_ID = "arm_motor";
    public static String ELEVATOR_MOTOR_ID = "elevator_motor";
    public static String ASCENT_MOTOR_ID = "ascent_motor";

    public static String TEST_MOTOR_1_ID = "test_motor_1";
    public static String TEST_MOTOR_2_ID = "test_motor_2";
    public static double TEST_MOTOR_1_POWER = -1.0;
    public static double TEST_MOTOR_2_POWER = 1.0;

    public static String CLAMP_SERVO_ID = "clamp_servo";
    public static String BASKET_SERVO_ID = "basket_servo";
    public static String ROTATION_SERVO_ID = "rotation_servo";
    public static String CLAMP_COLOR_SENSOR_ID = "clamp_color_sensor";
    public static String ARM_COLOR_SENSOR_ID = "arm_color_sensor";
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
        ANY_OR_UNKNOWN
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
