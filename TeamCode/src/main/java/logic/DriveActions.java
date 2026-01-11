package logic;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;

import math.Distance;
import math.Pose2D;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import roadrunner.MecanumDrive;

public class DriveActions {
    private final MecanumDrive drive;
    private final RobotPosition robotPosition;

    private final Team team;

    private static final Distance ARTIFACT_COLLECTION_DISTANCE = Distance.fromInches(22);

    public DriveActions(MecanumDrive drive, RobotPosition robotPosition, Team team) {
        this.drive = drive;
        this.robotPosition = robotPosition;
        this.team = team;
    }

    public Action driveToGoalShootPosition(double tangentAngleRadians) {
        Pose2D goalShootPosition = PlayingField.autoModeShootPose(team);
        return baseActionBuilder()
                .splineToLinearHeading(goalShootPosition.toRoadrunnerPose2d(), tangentAngleRadians)
                .build();
    }

    public Action driveToLeavePose() {
        Pose2D leavePose = PlayingField.autoModeLeavePose(team);
        double tangentAngle = Math.toRadians(270);
        return baseActionBuilder()
                .splineToLinearHeading(leavePose.toRoadrunnerPose2d(), tangentAngle)
                .build();
    }

    public Action driveToArtifactRowEntryPose(Artifact.Row row) {
        Pose2D entryPose = PlayingField.artifactRowEntryPose(team, row);
        double tangentAngle = entryPose.getHeading(AngleUnit.RADIANS);
        return baseActionBuilder()
                .splineToLinearHeading(entryPose.toRoadrunnerPose2d(), tangentAngle)
                .build();
    }

    public Action collectArtifactsFromRow(Artifact.Row row) {
        Pose2D entryPose = PlayingField.artifactRowEntryPose(team, row);

        Vector2d forwardVector = moveForwardVector(ARTIFACT_COLLECTION_DISTANCE);
        Vector2d endPos = entryPose.toPosition2D().toRoadrunnerVector().plus(forwardVector);

        return baseActionBuilder().strafeTo(endPos).waitSeconds(0.5).build();
    }

    public Action driveBackToArtifactRowEntryPose(Artifact.Row row) {
        Pose2D entryPose = PlayingField.artifactRowEntryPose(team, row);
        return baseActionBuilder().strafeTo(entryPose.toPosition2D().toRoadrunnerVector()).build();
    }

    private Vector2d moveForwardVector(Distance distance) {
        double heading = robotPosition.getPose().getHeading(AngleUnit.RADIANS);
        return new Vector2d(
                distance.toInches() * Math.cos(heading), distance.toInches() * Math.sin(heading));
    }

    private TrajectoryActionBuilder baseActionBuilder() {
        return drive.actionBuilder(robotPosition.getPose().toRoadrunnerPose2d());
    }
}
