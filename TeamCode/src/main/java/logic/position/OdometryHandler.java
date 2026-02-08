package logic.position;

import static config.OdometryConfig.ENCODER_X_Y_OFFSET;
import static config.OdometryConfig.ENCODER_Y_X_OFFSET;

import com.qualcomm.robotcore.hardware.HardwareMap;

import config.HardwareConfig;

import math.Pose2D;

import modules.sensor.GoBildaPinpointDriver;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public final class OdometryHandler {
    public final GoBildaPinpointDriver driver;

    private final Telemetry globalTelemetry;

    public OdometryHandler(HardwareMap hardwareMap, Telemetry globalTelemetry, Pose2D initialPose) {
        this.globalTelemetry = globalTelemetry;

        driver = hardwareMap.get(GoBildaPinpointDriver.class, HardwareConfig.ODOMETRY_POD_ID);

        driver.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        driver.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.REVERSED,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);

        driver.setOffsets(ENCODER_X_Y_OFFSET.toMillimeters(), ENCODER_Y_X_OFFSET.toMillimeters());

        setPose(initialPose);
        update();
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
