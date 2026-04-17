package modules.sensor;

import static config.HardwareConfig.DISTANCE_SENSOR_INTAKE_LEFT1_ID;
import static config.HardwareConfig.DISTANCE_SENSOR_INTAKE_LEFT2_ID;
import static config.HardwareConfig.DISTANCE_SENSOR_INTAKE_RIGHT1_ID;
import static config.HardwareConfig.DISTANCE_SENSOR_INTAKE_RIGHT2_ID;
import static config.HardwareConfig.DISTANCE_SENSOR_LEFT_ID;
import static config.HardwareConfig.DISTANCE_SENSOR_RIGHT_ID;
import static config.HardwareConfig.RGB_LIGHT_ID;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import utils.TelemetryHandler;

public class DistanceSensorMonitor {
    public DistanceSensor left;
    public DistanceSensor right;
    public DistanceSensor intake_left1;
    public DistanceSensor intake_left2;
    public DistanceSensor intake_right1;
    public DistanceSensor intake_right2;
    public Servo led;

    public DistanceSensorMonitor(HardwareMap hardwareMap) {
        left = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_LEFT_ID);
        right = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_RIGHT_ID);
        intake_left1 = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_INTAKE_LEFT1_ID);
        intake_left2 = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_INTAKE_LEFT2_ID);
        intake_right1 = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_INTAKE_RIGHT1_ID);
        intake_right2 = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_INTAKE_RIGHT2_ID);
        led = hardwareMap.get(Servo.class, RGB_LIGHT_ID);
    }

    public int getNumberOfArtifactsInRobot() {
        int count = 0;

        final int maxBackDist = 80;
        final int maxIntakeDist = 110;
        if (left.getDistance(DistanceUnit.MM) < maxBackDist) count++;
        if (right.getDistance(DistanceUnit.MM) < maxBackDist) count++;
        if (intake_left1.getDistance(DistanceUnit.MM) < maxIntakeDist
                || intake_left2.getDistance(DistanceUnit.MM) < maxIntakeDist) count++;
        if (intake_right1.getDistance(DistanceUnit.MM) < maxIntakeDist
                || intake_right2.getDistance(DistanceUnit.MM) < maxIntakeDist) count++;

        TelemetryHandler.addData("_Left Distance", left.getDistance(DistanceUnit.MM));
        TelemetryHandler.addData("_Right Distance", right.getDistance(DistanceUnit.MM));
        TelemetryHandler.addData(
                "_Intake Left 1 Distance", intake_left1.getDistance(DistanceUnit.MM));
        TelemetryHandler.addData(
                "_Intake Left 2 Distance", intake_left2.getDistance(DistanceUnit.MM));
        TelemetryHandler.addData(
                "_Intake Right 1 Distance", intake_right1.getDistance(DistanceUnit.MM));
        TelemetryHandler.addData(
                "_Intake Right 2 Distance", intake_right2.getDistance(DistanceUnit.MM));

        return count;
    }
}
