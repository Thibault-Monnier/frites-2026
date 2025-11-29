package core.localization;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import core.roadrunner.PinpointLocalizer;

public class OdometryHandler {
    private static OdometryHandler instance;

    private final PinpointLocalizer localizer;

    public static OdometryHandler getInstance(HardwareMap hardwareMap) {
        if (instance == null) {
            instance = new OdometryHandler(hardwareMap);
        }
        return instance;
    }

    private OdometryHandler(HardwareMap hardwareMap) {
        localizer = new PinpointLocalizer(hardwareMap, 0.00198, new Pose2d(0, 0, 0));
    }

    public void update() {
        localizer.update();
    }

    public Pose2D getPose() {
        Pose2d pose = localizer.getPose();
        return new Pose2D(
                DistanceUnit.INCH,
                pose.position.x,
                pose.position.y,
                AngleUnit.RADIANS,
                pose.heading.toDouble());
    }
}
