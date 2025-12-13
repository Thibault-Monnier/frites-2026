package modules.sensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

import math.Distance;
import math.Pose2D;
import modules.HardwareConstants;
import roadrunner.GoBildaPinpointDriver;

public final class OdometryHandler {
    /** The offset of the x encoder along the y axis */
    private static final Distance ENCODER_X_Y_OFFSET = Distance.fromMillimeters(95);

    /** The offset of the y encoder along the x axis */
    private static final Distance ENCODER_Y_X_OFFSET = Distance.fromMillimeters(200);

    public final GoBildaPinpointDriver driver;

    public OdometryHandler(HardwareMap hardwareMap, Pose2D initialPose) {
        driver = hardwareMap.get(GoBildaPinpointDriver.class, HardwareConstants.ODOMETRY_POD_ID);

        driver.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        driver.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        driver.setOffsets(ENCODER_X_Y_OFFSET.toMillimeters(), ENCODER_Y_X_OFFSET.toMillimeters());

        driver.resetPosAndIMU();

        setPose(initialPose);
    }

    /**
     * Returns the current pose estimate. NOTE: Does not update the pose estimate; you must call
     * update() to update the pose estimate.
     *
     * @return the current pose estimate
     */
    public Pose2D getPose() {
        return Pose2D.fromNavigationPose2D(driver.getPosition());
    }

    /** Overrides the current pose estimate. */
    public void setPose(Pose2D pose) {
        driver.setPosition(pose.toNavigationPose2D());
    }

    /** Updates the pose estimate. */
    public void update() {
        driver.update();
    }
}
