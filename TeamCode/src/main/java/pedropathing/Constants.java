package pedropathing;

import static config.OdometryConfig.ENCODER_X_Y_OFFSET;
import static config.OdometryConfig.ENCODER_Y_X_OFFSET;

import androidx.annotation.Nullable;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import config.HardwareConfig;
import config.MovementConfig;

import logic.position.RobotPosition;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class Constants {
    public static FollowerConstants followerConstants =
            new FollowerConstants()
                    .mass(15) // TODO: Set this
                    .forwardZeroPowerAcceleration(-29.75878312266954)
                    .lateralZeroPowerAcceleration(-63.18270607279411)
                    .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0, 0.015, 0.075))
                    .headingPIDFCoefficients(new PIDFCoefficients(1, 0, 0.1, 0.075))
                    .drivePIDFCoefficients(
                            new FilteredPIDFCoefficients(0.03, 0, 0.0001, 0.6, 0.075));

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static PinpointConstants pinpointConstants =
            new PinpointConstants()
                    .forwardPodY(ENCODER_X_Y_OFFSET.getValue(DistanceUnit.INCH))
                    .strafePodX(ENCODER_Y_X_OFFSET.getValue(DistanceUnit.INCH))
                    .distanceUnit(DistanceUnit.INCH)
                    .hardwareMapName(HardwareConfig.ODOMETRY_POD_ID)
                    .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
                    .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
                    .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);

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
                    .xVelocity(85.3)
                    .yVelocity(66.5);

    public static Follower createFollower(
            HardwareMap hardwareMap, @Nullable RobotPosition robotPosition) {
        FollowerBuilder builder =
                new FollowerBuilder(followerConstants, hardwareMap)
                        .pathConstraints(pathConstraints)
                        .mecanumDrivetrain(driveConstants);

        if (robotPosition != null) {
            RobotLocalizer localizer = new RobotLocalizer(robotPosition);
            builder = builder.setLocalizer(localizer);
        } else {
            builder = builder.pinpointLocalizer(pinpointConstants);
        }

        return builder.build();
    }
}
