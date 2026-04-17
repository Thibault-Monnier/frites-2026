package pedropathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.HardwareMap;

import config.HardwareConfig;
import config.MovementConfig;

import logic.position.RobotPosition;

public class Constants {
    public static FollowerConstants followerConstants =
            new FollowerConstants()
                    .mass(15) // TODO: Set this
                    .forwardZeroPowerAcceleration(-29.75878312266954)
                    .lateralZeroPowerAcceleration(-63.18270607279411)
                    .translationalPIDFCoefficients(new PIDFCoefficients(0.075, 0, 0.001, 0.025))
                    .headingPIDFCoefficients(new PIDFCoefficients(0.85, 0, 0.001, 0.025))
                    .drivePIDFCoefficients(
                            new FilteredPIDFCoefficients(0.05, 0, 0.00001, 0.6, 0.025));

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static MecanumConstants driveConstants =
            new MecanumConstants()
                    .maxPower(1)
                    .leftFrontMotorName(HardwareConfig.FRONT_LEFT_MOTOR_ID)
                    .rightFrontMotorName(HardwareConfig.FRONT_RIGHT_MOTOR_ID)
                    .leftRearMotorName(HardwareConfig.BACK_LEFT_MOTOR_ID)
                    .rightRearMotorName(HardwareConfig.BACK_RIGHT_MOTOR_ID)
                    .leftFrontMotorDirection(MovementConfig.FRONT_LEFT_DIRECTION)
                    .rightFrontMotorDirection(MovementConfig.FRONT_RIGHT_DIRECTION)
                    .leftRearMotorDirection(MovementConfig.BACK_LEFT_DIRECTION)
                    .rightRearMotorDirection(MovementConfig.BACK_RIGHT_DIRECTION)
                    .xVelocity(64.05772772360976)
                    .yVelocity(49.779096948818896);

    public static Follower createFollower(HardwareMap hardwareMap, RobotPosition robotPosition) {
        RobotLocalizer localizer = new RobotLocalizer(robotPosition);
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .setLocalizer(localizer)
                .build();
    }
}
