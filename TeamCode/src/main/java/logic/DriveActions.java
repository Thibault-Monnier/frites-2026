package logic;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;

import math.Position2D;
import roadrunner.MecanumDrive;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DriveActions {
    private final MecanumDrive drive;

    public DriveActions(MecanumDrive drive) {
        this.drive = drive;
    }

    public Action turnTowardsGoal(Pose2d robotPose, Team teamColor) {
        Position2D robotPosition = new Position2D(DistanceUnit.INCH, robotPose.position);
        return drive.actionBuilder(robotPose)
                .turnTo(PlayingField.angleToGoal(robotPosition, teamColor))
                .build();
    }
}
