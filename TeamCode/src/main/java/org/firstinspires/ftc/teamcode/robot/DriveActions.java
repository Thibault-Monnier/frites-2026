package org.firstinspires.ftc.teamcode.robot;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.field.PlayingField;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

public class DriveActions {
    private final MecanumDrive drive;

    public DriveActions(MecanumDrive drive) {
        this.drive = drive;
    }

    public Action turnTowardsGoal(Pose2d robotPose, Constants.Team teamColor) {
        return drive.actionBuilder(robotPose)
                .turnTo(PlayingField.angleToGoal(robotPose.position, teamColor))
                .build();
    }
}
