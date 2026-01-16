package logic;

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

    private static final Distance ARTIFACT_COLLECTION_DISTANCE = Distance.fromInches(12);

    public DriveActions(MecanumDrive drive, RobotPosition robotPosition, Team team) {
        this.drive = drive;
        this.robotPosition = robotPosition;
        this.team = team;
    }

    public DeferredAction driveToGoalShootPosition() {
        return new DeferredAction(
                () -> {
                    Pose2D goalShootPosition = PlayingField.autoModeShootPose(team);
                    return baseActionBuilder()
                            .strafeTo(goalShootPosition.toPosition2D().toRoadrunnerVector())
                            .build();
                });
    }

    public DeferredAction driveToLeavePose() {
        return new DeferredAction(
                () -> {
                    Pose2D leavePose = PlayingField.autoModeLeavePose(team);
                    return baseActionBuilder()
                            .strafeTo(leavePose.toPosition2D().toRoadrunnerVector())
                            .turnTo(leavePose.getHeading(AngleUnit.RADIANS))
                            .build();
                });
    }

    public DeferredAction driveToArtifactRowEntryPose(Artifact.Row row) {
        return new DeferredAction(
                () -> {
                    Pose2D entryPose = PlayingField.artifactRowEntryPose(team, row);
                    return baseActionBuilder()
                            .strafeTo(entryPose.toPosition2D().toRoadrunnerVector())
                            .build();
                });
    }

    public DeferredAction collectArtifactsFromRow(Artifact.Row row) {
        return new DeferredAction(
                () -> {
                    Pose2D entryPose = PlayingField.artifactRowEntryPose(team, row);

                    Vector2d forwardVector = moveForwardVector(ARTIFACT_COLLECTION_DISTANCE);
                    Vector2d endPos =
                            entryPose.toPosition2D().toRoadrunnerVector().plus(forwardVector);

                    return baseActionBuilder().strafeTo(endPos).waitSeconds(0.4).build();
                });
    }

    public DeferredAction driveBackToArtifactRowEntryPose(Artifact.Row row) {
        return new DeferredAction(
                () -> {
                    Pose2D entryPose = PlayingField.artifactRowEntryPose(team, row);
                    return baseActionBuilder()
                            .strafeTo(entryPose.toPosition2D().toRoadrunnerVector())
                            .build();
                });
    }

    private Vector2d moveForwardVector(Distance distance) {
        double heading = robotPosition.getPose().getHeading(AngleUnit.RADIANS);
        return new Vector2d(
                distance.toInches() * Math.cos(heading), distance.toInches() * Math.sin(heading));
    }

    private TrajectoryActionBuilder baseActionBuilder() {
        System.out.println("Building action at pose: " + drive.localizer.getPose());
        return drive.actionBuilder(drive.localizer.getPose().toRoadrunnerPose2d());
    }
}
