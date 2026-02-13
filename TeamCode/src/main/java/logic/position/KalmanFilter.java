package logic.position;

import android.util.Pair;

import config.KalmanFilterConfig;

import math.Angle;
import math.Distance;
import math.Pose2D;

public class KalmanFilter {
    private Pose2D pose;
    private Pose2D poseVariance;

    public KalmanFilter(Pose2D initialPose) {
        this.pose = initialPose;
        this.poseVariance =
                new Pose2D(
                        Distance.fromMillimeters(100),
                        Distance.fromMillimeters(100),
                        Angle.fromDegrees(30));
    }

    public Pose2D unite(Pose2D newCameraPose, Pose2D odometryVelocity) {
        Pair<Distance, Distance> updatedX =
                updateDist(
                        pose.getX(),
                        poseVariance.getX(),
                        newCameraPose.getX(),
                        odometryVelocity.getX());
        Pair<Distance, Distance> updatedY =
                updateDist(
                        pose.getY(),
                        poseVariance.getY(),
                        newCameraPose.getY(),
                        odometryVelocity.getY());
        Pair<Angle, Angle> updatedHeading =
                updateAngle(
                        pose.getHeading(),
                        poseVariance.getHeading(),
                        newCameraPose.getHeading(),
                        odometryVelocity.getHeading());

        pose = new Pose2D(updatedX.first, updatedY.first, updatedHeading.first);
        poseVariance = new Pose2D(updatedX.second, updatedY.second, updatedHeading.second);

        return pose;
    }

    public Pair<Distance, Distance> updateDist(
            Distance value,
            Distance variance,
            Distance newCameravalue,
            Distance odometryVelocityValue) {
        Distance predictedValue = value.add(odometryVelocityValue);
        Distance predictedVariance = variance.add(KalmanFilterConfig.MODEL_VARIANCE_DIST);

        double gain =
                predictedVariance.ratio(
                        predictedVariance.add(KalmanFilterConfig.CAMERA_VARIANCE_DIST));

        Distance updatedValue = value.add(newCameravalue.subtract(predictedValue).multiply(gain));
        Distance updatedVariance = predictedVariance.multiply(1 - gain);
        return new Pair<>(updatedValue, updatedVariance);
    }

    public Pair<Angle, Angle> updateAngle(
            Angle value, Angle variance, Angle newCameraValue, Angle odometryVelocityValue) {
        Angle predictedValue = value.add(odometryVelocityValue);
        Angle predictedVariance = variance.add(KalmanFilterConfig.MODEL_VARIANCE_ANGLE);

        double gain =
                predictedVariance.ratio(
                        predictedVariance.add(KalmanFilterConfig.CAMERA_VARIANCE_ANGLE));

        Angle updatedValue = value.add(newCameraValue.subtract(predictedValue).multiply(gain));
        Angle updatedVariance = predictedVariance.multiply(1 - gain);
        return new Pair<>(updatedValue, updatedVariance);
    }
}
