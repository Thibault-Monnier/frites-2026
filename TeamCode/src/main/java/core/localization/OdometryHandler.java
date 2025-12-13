package core.localization;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

import core.Constants;
import core.math.Distance;
import core.math.Pose2D;
import core.roadrunner.GoBildaPinpointDriver;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Objects;

public final class OdometryHandler {
    /** The offset of the x encoder along the y axis */
    private static final Distance ENCODER_X_Y_OFFSET = Distance.fromMillimeters(95);

    /** The offset of the y encoder along the x axis */
    private static final Distance ENCODER_Y_X_OFFSET = Distance.fromMillimeters(200);

    public final GoBildaPinpointDriver driver;
    private Pose2d txWorldPinpointMillimeters;
    private Pose2d txPinpointRobotMillimeters = new Pose2d(0, 0, 0);

    public OdometryHandler(HardwareMap hardwareMap, Pose2D initialPose) {
        driver = hardwareMap.get(GoBildaPinpointDriver.class, Constants.ODOMETRY_POD_ID);

        driver.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        driver.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        driver.setOffsets(ENCODER_X_Y_OFFSET.toMillimeters(), ENCODER_Y_X_OFFSET.toMillimeters());

        driver.resetPosAndIMU();

        txWorldPinpointMillimeters = initialPose.toPose2d(DistanceUnit.MM, AngleUnit.RADIANS);
    }

    /**
     * Returns the current pose estimate. NOTE: Does not update the pose estimate; you must call
     * update() to update the pose estimate.
     *
     * @return the current pose estimate
     */
    public Pose2D getPose() {
        Pose2d pose2dMillimeters = txWorldPinpointMillimeters.times(txPinpointRobotMillimeters);
        return Pose2D.fromPose2d(pose2dMillimeters, DistanceUnit.MM, AngleUnit.RADIANS);
    }

    /** Overrides the current pose estimate. */
    public void setPose(Pose2D pose) {
        Pose2d pose2d = pose.toPose2d(DistanceUnit.MM, AngleUnit.RADIANS);
        txWorldPinpointMillimeters = pose2d.times(txPinpointRobotMillimeters.inverse());
    }

    /** Updates the pose estimate. */
    public void update() {
        driver.update();
        if (Objects.requireNonNull(driver.getDeviceStatus())
                == GoBildaPinpointDriver.DeviceStatus.READY) {
            txPinpointRobotMillimeters =
                    new Pose2d(driver.getPosX(), driver.getPosY(), driver.getHeading());
        }
    }
}
