package config;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import utils.geometry.Distance;

@Config
@Configurable
public class HardwareConfig {
    public static String FRONT_LEFT_MOTOR_ID = "front_left";
    public static String FRONT_RIGHT_MOTOR_ID = "front_right";
    public static String BACK_LEFT_MOTOR_ID = "back_left";
    public static String BACK_RIGHT_MOTOR_ID = "back_right";
    public static String CANNON_MOTOR_LEFT_ID = "cannon_motor_left";
    public static String CANNON_MOTOR_RIGHT_ID = "cannon_motor_right";
    public static String CANNON_BUFFER_MOTOR_ID = "cannon_buffer_left";
    public static String INTAKE_MOTOR_ID = "intake_motor";
    public static final String LIMELIGHT_CAMERA_ID = "limelight";
    public static final String ODOMETRY_POD_ID = "pinpoint";

    public static final String DISTANCE_SENSOR_LEFT_ID = "distance_left";
    public static final String DISTANCE_SENSOR_RIGHT_ID = "distance_right";
    public static final String DISTANCE_SENSOR_INTAKE_LEFT1_ID = "distance_intake_left1";
    public static final String DISTANCE_SENSOR_INTAKE_LEFT2_ID = "distance_intake_left2";
    public static final String DISTANCE_SENSOR_INTAKE_RIGHT1_ID = "distance_intake_right1";
    public static final String DISTANCE_SENSOR_INTAKE_RIGHT2_ID = "distance_intake_right2";
    public static final String RGB_LIGHT_ID = "rgb_light";

    public static double SHOOTER_MAX_VELOCITY = 2450.0;

    public static Distance ROBOT_SIZE = new Distance(DistanceUnit.INCH, 18.0);

    public static String TEST_MOTOR_1_ID = "test_motor_1";
    public static String TEST_MOTOR_2_ID = "test_motor_2";
    public static double TEST_MOTOR_1_POWER = -1.0;
    public static double TEST_MOTOR_2_POWER = 1.0;
}
