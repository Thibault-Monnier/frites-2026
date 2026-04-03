package pedropathing;

import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Vector;

import logic.position.RobotPosition;

public class RobotLocalizer implements Localizer {
    private final RobotPosition robotPosition;

    public RobotLocalizer(RobotPosition robotPosition) {
        this.robotPosition = robotPosition;
    }

    public void setStartPose(Pose pose) {
        throw new RuntimeException("Shouldn't be calling setStartPose()");
    }

    public void setPose(Pose pose) {
        throw new RuntimeException("Shouldn't be calling setPose()");
    }

    public void update() {
        // Do nothing: this is handled separately
    }

    public double getTotalHeading() {
        throw new RuntimeException("Shouldn't be calling getTotalHeading()");
    }

    public double getForwardMultiplier() {
        throw new RuntimeException("Shouldn't be calling getForwardMultiplier()");
    }

    public double getLateralMultiplier() {
        throw new RuntimeException("Shouldn't be calling getLateralMultiplier()");
    }

    public double getTurningMultiplier() {
        throw new RuntimeException("Shouldn't be calling getTurningMultiplier()");
    }

    public void resetIMU() {
        // Do nothing: this is handled separately
    }

    public double getIMUHeading() {
        throw new RuntimeException("Shouldn't be calling getIMUHeading()");
    }

    public boolean isNAN() {
        return robotPosition.getPose().hasNaN();
    }

    public void setX(double x) {
        throw new RuntimeException("Shouldn't be calling setX()");
    }

    public void setY(double y) {
        throw new RuntimeException("Shouldn't be calling setX()");
    }

    public void setHeading(double heading) {
        throw new RuntimeException("Shouldn't be calling setHeading()");
    }

    public Pose getPose() {
        return robotPosition.getPose().toPedropathingPose();
    }

    public Pose getVelocity() {
        return robotPosition.getPedroPoseVelocity();
    }

    public Vector getVelocityVector() {
        return getVelocity().getAsVector();
    }
}
