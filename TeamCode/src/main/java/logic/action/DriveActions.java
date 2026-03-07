package logic.action;

import config.FieldConfig;

import logic.Movement;
import logic.Team;
import logic.field.Artifact;
import logic.field.PlayingField;
import logic.position.RobotPosition;

import math.Pose2D;
import math.Position2D;
import math.Vector2D;

public class DriveActions {
    private final Movement drive;

    private final RobotPosition robotPosition;
    private final Team team;

    public DriveActions(Movement drive, RobotPosition robotPosition, Team team) {
        this.drive = drive;
        this.robotPosition = robotPosition;
        this.team = team;
    }

    public Action driveToGoalShootPosition() {
        return () -> {
            Position2D goalShootPosition = PlayingField.shootingPosition(team);
            return drive.translateToPosition(goalShootPosition)
                    && drive.turnTowards(PlayingField.goalPos(team));
        };
    }

    public Action driveToLeavePose() {
        return () -> {
            Pose2D leavePose = PlayingField.autoModeLeavePose(team);
            return drive.translateToPosition(leavePose.toPosition2D())
                    && drive.turnTowardsHeading(leavePose.getHeading());
        };
    }

    public Action driveToArtifactRowEntryPose(Artifact.Row row) {
        return () -> {
            Pose2D entryPose = PlayingField.artifactRowEntryPose(team, row);
            return drive.translateToPosition(entryPose.toPosition2D())
                    && drive.turnTowardsHeading(entryPose.getHeading());
        };
    }

    public Action collectArtifactsFromRow(Artifact.Row row) {
        return () -> {
            Position2D entryPose = PlayingField.artifactRowEntryPose(team, row).toPosition2D();

            Vector2D forwardVector =
                    new Vector2D(
                            FieldConfig.ARTIFACT_COLLECTION_DISTANCE, robotPosition.getHeading());
            Position2D endPos = entryPose.add(forwardVector);

            return drive.translateToPosition(endPos);
        };
    }

    public Action driveBackToArtifactRowEntryPose(Artifact.Row row) {
        return () -> {
            Pose2D entryPose = PlayingField.artifactRowEntryPose(team, row);
            return drive.translateToPosition(entryPose.toPosition2D());
        };
    }
}
