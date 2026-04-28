package logic;

import config.CannonConfig;
import config.FieldConfig;

import logic.field.PlayingField;
import logic.position.RobotPosition;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import utils.TelemetryHandler;
import utils.math.Angle;
import utils.math.Distance;
import utils.math.Pose2D;
import utils.math.Position2D;
import utils.math.Vector2D;
import utils.math.Velocity2D;

public class ShotHandler {
    private final RobotPosition robotPosition;

    private final Team team;

    private Vector2D computedShotVector;

    boolean usingMovingShot = true;

    public ShotHandler(RobotPosition robotPosition, Team team) {
        this.robotPosition = robotPosition;
        this.team = team;
    }

    /** Updates the shot vector based on the robot's current velocity and pose. */
    public void update() {
        if (usingMovingShot) computedShotVector = computeMovingShotVector();
        else computedShotVector = computeStationaryShotVector();

        TelemetryHandler.addData("Computed shot vector", computedShotVector.toString());
    }

    /** Gets the computed shot vector. Should be called after update(). */
    public Vector2D getShotVector() {
        return computedShotVector;
    }

    /** Gets the magnitude of the computed shot vector. Should be called after update(). */
    public Distance getShotMagnitude() {
        return computedShotVector.magnitude();
    }

    /** Gets the angle of the computed shot vector. Should be called after update(). */
    public Angle getShotAngle() {
        return computedShotVector.direction();
    }

    /** Toggles between using the moving shot calculation and the stationary shot calculation. */
    public void toggleUsingMovingShot() {
        usingMovingShot = !usingMovingShot;
    }

    private Vector2D computeMovingShotVector() {
        TelemetryHandler.addLine("Using moving shot calculation");

        final double g = 9.81; // gravitational acceleration in m/s^2

        Vector2D cannonLinearVelocity = cannonVelocity().getLinearVelocity();
        Angle velocityAngle = cannonLinearVelocity.direction();
        Angle correctionAngle = velocityAngle.negate();
        cannonLinearVelocity = cannonLinearVelocity.rotate(correctionAngle);

        Angle theta = CannonConfig.CANNON_ANGLE;
        Distance cannonTopHeight = CannonConfig.CANNON_TOP_HEIGHT;
        Distance goalHeight = FieldConfig.GOAL_HEIGHT;
        Position2D cannonPos = cannonPos().rotateAroundOrigin(correctionAngle);
        Position2D goalPos = goalPos().rotateAroundOrigin(correctionAngle);

        Angle phi = cannonPos.angleTo(goalPos);
        Vector2D dHorizontal = cannonPos.subtract(goalPos);
        double dx = dHorizontal.magnitude().toMeters();
        double dy = goalHeight.subtract(cannonTopHeight).toMeters();

        double ballSpeed = dx * Math.sqrt(g / (2 * (dx * theta.tan() - dy)));
        double robotSpeed = cannonLinearVelocity.magnitude().toMeters();

        double shootSpeed =
                Math.sqrt(
                        ballSpeed * ballSpeed
                                - 2 * robotSpeed * ballSpeed * phi.cos()
                                + robotSpeed * robotSpeed);
        double shootDistance = shootSpeed * dx / ballSpeed;
        double shootAngle = Math.atan2(ballSpeed * phi.sin(), ballSpeed * phi.cos() - robotSpeed);

        Distance norm = new Distance(DistanceUnit.METER, shootDistance);
        Angle argument = new Angle(AngleUnit.RADIANS, shootAngle);

        return new Vector2D(norm, argument).rotate(velocityAngle);
    }

    private Vector2D computeStationaryShotVector() {
        TelemetryHandler.addLine("Using stationary shot calculation");
        return goalPos().subtract(cannonPos());
    }

    private Position2D cannonPos() {
        Pose2D robotPose = robotPosition.getPose();
        return robotPose.addRelative(CannonConfig.CANNON_RELATIVE_POSITION);
    }

    private Velocity2D cannonVelocity() {
        return robotPosition.getPointVelocity(CannonConfig.CANNON_RELATIVE_POSITION);
    }

    private Position2D goalPos() {
        return PlayingField.goalPos(team);
    }
}
