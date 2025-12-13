package core.modules;

import com.acmerobotics.dashboard.config.Config;

@Config
public class HardwareConstants {
    public static String FRONT_LEFT_MOTOR_ID = "front_left";
    public static String FRONT_RIGHT_MOTOR_ID = "front_right";
    public static String BACK_LEFT_MOTOR_ID = "back_left";
    public static String BACK_RIGHT_MOTOR_ID = "back_right";
    public static String CANNON_MOTOR_LEFT_ID = "cannon_motor_left";
    public static String CANNON_MOTOR_RIGHT_ID = "cannon_motor_right";
    public static String CANNON_BUFFER_RIGHT = "cannon_buffer_right";
    public static String CANNON_BUFFER_LEFT = "cannon_buffer_left";
    public static String INTAKE_MOTOR_ID = "intake_motor";
    public static final String INTAKE_SWITCHER_SERVO = "intake_switcher_servo";
    public static final String LIMELIGHT_CAMERA_ID = "limelight";
    public static final String ODOMETRY_POD_ID = "pinpoint";
    public static String IMU_ID = "imu";

    public static String TEST_MOTOR_1_ID = "test_motor_1";
    public static String TEST_MOTOR_2_ID = "test_motor_2";
    public static double TEST_MOTOR_1_POWER = -1.0;
    public static double TEST_MOTOR_2_POWER = 1.0;
}
