package logic.position;

import android.util.Pair;

import config.KalmanFilterConfig;

import utils.math.Angle;
import utils.math.Distance;
import utils.math.Pose2D;
import utils.math.Transform2D;
import utils.math.Vector2D;

public class KalmanFilter {
    public Pose2D pose;
    private Pose2D poseVariance;

    public KalmanFilter(Pose2D initialPose) {
        this.pose = initialPose;
        this.poseVariance =
                new Pose2D(
                        KalmanFilterConfig.MODEL_VARIANCE_DIST,
                        KalmanFilterConfig.MODEL_VARIANCE_DIST,
                        KalmanFilterConfig.MODEL_VARIANCE_ANGLE);
    }

    public Pose2D predict(Transform2D displacement) {
        pose = pose.add(displacement);
        poseVariance =
                poseVariance
                        .add(
                                new Vector2D(
                                        KalmanFilterConfig.MODEL_VARIANCE_DIST,
                                        KalmanFilterConfig.MODEL_VARIANCE_DIST))
                        .add(KalmanFilterConfig.MODEL_VARIANCE_ANGLE);
        return pose;
    }

    public Pose2D update(Pose2D newCameraPose) {
        Pair<Distance, Distance> updatedX =
                updateDist(pose.getX(), poseVariance.getX(), newCameraPose.getX());
        Pair<Distance, Distance> updatedY =
                updateDist(pose.getY(), poseVariance.getY(), newCameraPose.getY());
        Pair<Angle, Angle> updatedHeading =
                updateAngle(
                        pose.getHeading(), poseVariance.getHeading(), newCameraPose.getHeading());

        pose = new Pose2D(updatedX.first, updatedY.first, updatedHeading.first);
        poseVariance = new Pose2D(updatedX.second, updatedY.second, updatedHeading.second);

        return pose;
    }

    public Pair<Distance, Distance> updateDist(
            Distance value, Distance variance, Distance newCameravalue) {
        double gain = variance.ratio(variance.add(KalmanFilterConfig.CAMERA_VARIANCE_DIST));

        Distance updatedValue = value.add(newCameravalue.subtract(value).multiply(gain));
        Distance updatedVariance = variance.multiply(1 - gain);

        return new Pair<>(updatedValue, updatedVariance);
    }

    public Pair<Angle, Angle> updateAngle(Angle value, Angle variance, Angle newCameraValue) {
        double gain = variance.ratio(variance.add(KalmanFilterConfig.CAMERA_VARIANCE_ANGLE));

        Angle updatedValue = value.add(newCameraValue.subtract(value).multiply(gain));
        Angle updatedVariance = variance.multiply(1 - gain);

        return new Pair<>(updatedValue, updatedVariance);
    }
}
