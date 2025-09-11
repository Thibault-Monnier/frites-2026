package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

public class DriveActions {
    public static final Pose2d START_BASKET = new Pose2d(14, 63, Math.toRadians(-90));
    public static final Pose2d START_OBSERVATION_ZONE = new Pose2d(-14, 63, Math.toRadians(-90));
    public static final Pose2d OBSERVATION_ZONE = new Pose2d(-32, 63, Math.toRadians(-90));
    public static final Pose2d SUBMERSIBLE_ZONE = new Pose2d(0, 32, Math.toRadians(-90));

    private final MecanumDrive drive;

    public DriveActions(MecanumDrive drive) {
        this.drive = drive;
    }

    public Action gotToSubmersible(Pose2d initPos) {
        return drive.actionBuilder(initPos)
                .setTangent(Math.toRadians(90))
                .splineTo(SUBMERSIBLE_ZONE.component1(), SUBMERSIBLE_ZONE.component2())
                .build();
    }

    public Action goFromSubmersibleToObservation(boolean takeSamples) {
        return gotFromSubmersibleToObservation(SUBMERSIBLE_ZONE, takeSamples);
    }

    public Action gotFromSubmersibleToObservation(Pose2d initPos, boolean takeSamples) {
        TrajectoryActionBuilder builder = drive.actionBuilder(initPos);

        if (takeSamples) {
            return builder
                    .setTangent(Math.toRadians(90))
                    .splineTo(new Vector2d(10, 32), Math.toRadians(-90))
                    .build();
        } else {
            return builder
                    .setTangent(Math.toRadians(90))
                    .splineTo(new Vector2d(0, 32), Math.toRadians(-90))
                    .build();
        }
    }

    public Action goFromObservationZoneToSubmersible() {
        return goFromObservationZoneToSubmersible(OBSERVATION_ZONE);
    }

    public Action goFromObservationZoneToSubmersible(Pose2d initPos) {
        return drive.actionBuilder(initPos)
                .setTangent(Math.toRadians(90))
                .splineTo(SUBMERSIBLE_ZONE.component1(), SUBMERSIBLE_ZONE.component2())
                .build();
    }
}
