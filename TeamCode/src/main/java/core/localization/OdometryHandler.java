package core.localization;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import core.math.Pose2D;
import core.roadrunner.PinpointLocalizer;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class OdometryHandler {
    private final PinpointLocalizer localizer;
    private Pose2D poseBase; // The base to which localizer pose is relative

    public OdometryHandler(HardwareMap hardwareMap, Pose2D initialPose) {
        localizer = new PinpointLocalizer(hardwareMap, 0.00198, new Pose2d(0, 0, 0));
        resetAll();
        setPoseBase(initialPose);
    }

    /**
     * This resets both the localizer pose and the driver odometry readings to zero. It then
     * recalibrates the driver IMU.
     *
     * <p>This function should ONLY be called when we a certain the robot is stationary.
     */
    private void resetAll() {
        localizer.setPose(new Pose2d(0, 0, 0));
        localizer.driver.resetPosAndIMU();
    }

    public void setPoseBase(Pose2D poseBase) {
        this.poseBase = poseBase;
        localizer.setPose(new Pose2d(0, 0, 0));
    }

    public void update() {
        localizer.update();
    }

    public Pose2D getPose() {
        Pose2d pose = localizer.getPose();
        Pose2D relPose =
                new Pose2D(
                        DistanceUnit.INCH,
                        pose.position.x,
                        pose.position.y,
                        AngleUnit.RADIANS,
                        pose.heading.toDouble());
        return relPose.add(poseBase);
    }
}
