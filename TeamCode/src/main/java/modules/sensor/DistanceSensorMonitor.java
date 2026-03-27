package modules.sensor;

import static config.HardwareConfig.DISTANCE_SENSOR_INTAKE_LEFT_ID;
import static config.HardwareConfig.DISTANCE_SENSOR_INTAKE_RIGHT_ID;
import static config.HardwareConfig.DISTANCE_SENSOR_LEFT_ID;
import static config.HardwareConfig.DISTANCE_SENSOR_RIGHT_ID;
import static config.HardwareConfig.NEOPIXEL_ID;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import team.techtigers.core.display.AdafruitNeoPixel;

public class DistanceSensorMonitor {
    public DistanceSensor left;
    public DistanceSensor right;
    public DistanceSensor intake_left;
    public DistanceSensor intake_right;
    public AdafruitNeoPixel neoPixel;
    public DistanceSensorMonitor(HardwareMap hardwareMap) {
        left = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_LEFT_ID);
        right = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_RIGHT_ID);
        intake_left = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_INTAKE_LEFT_ID);
        intake_right = hardwareMap.get(DistanceSensor.class, DISTANCE_SENSOR_INTAKE_RIGHT_ID);
        neoPixel = hardwareMap.get(AdafruitNeoPixel.class, NEOPIXEL_ID);
        neoPixel.initialize(24, 3);
        neoPixel.clearLeds();
    }

    public int getNumberOfArtifactsInRobot() {
        int count = 0;
        if (left.getDistance(DistanceUnit.MM) < 70) count++;
        if (right.getDistance(DistanceUnit.MM) < 70) count++;
        if (intake_left.getDistance(DistanceUnit.MM) < 70) count++;
        if (intake_right.getDistance(DistanceUnit.MM) < 70) count++;
        return count;
    }
}
