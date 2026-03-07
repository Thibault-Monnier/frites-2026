package logic;

import config.CannonConfig;
import config.FieldConfig;

import logic.field.PlayingField;
import logic.position.RobotPosition;

import math.Angle;
import math.Distance;
import math.Position2D;
import math.Vector2D;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ShotHandler {
    private final RobotPosition robotPosition;
    private final Team team;

    private final Telemetry globalTelemetry;

    private Vector2D computedShotVector;

    boolean usingMovingShot = true;

    public ShotHandler(RobotPosition robotPosition, Team team, Telemetry globalTelemetry) {
        this.robotPosition = robotPosition;
        this.team = team;
        this.globalTelemetry = globalTelemetry;
    }

    /** Updates the shot vector based on the robot's current velocity and pose. */
    public void update() {
        if (usingMovingShot) computedShotVector = computeMovingShotVector();
        else computedShotVector = computeStationaryShotVector();

        globalTelemetry.addData("Computed shot vector", computedShotVector.toString());
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
        globalTelemetry.addLine("Using moving shot calculation");

        final double g = 9.81; // gravitational acceleration in m/s^2

        // FIXME: this is not exact as we should be using the cannon's velocity instead
        Vector2D robotVelocity = robotPosition.getVelocity();
        Angle velocityAngle = robotVelocity.direction();
        Angle correctionAngle = velocityAngle.negate();
        robotVelocity = robotVelocity.rotate(correctionAngle);

        Angle theta = CannonConfig.CANNON_ANGLE;
        Distance cannonTopHeight = CannonConfig.CANNON_TOP_HEIGHT;
        Distance goalHeight = FieldConfig.GOAL_HEIGHT;
        Position2D cannonPos = cannonPos().toVector2D().rotate(correctionAngle).toPosition2D();
        Position2D goalPos = goalPos().toVector2D().rotate(correctionAngle).toPosition2D();

        Angle phi = cannonPos.angleTo(goalPos);
        Vector2D dHorizontal = cannonPos.subtract(goalPos).toVector2D();
        double dx = dHorizontal.magnitude().toMeters();
        double dy = goalHeight.subtract(cannonTopHeight).toMeters();

        double ballSpeed = dx * Math.sqrt(g / (2 * (dx * theta.tan() - dy)));
        double robotSpeed = robotVelocity.magnitude().toMeters();

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
        globalTelemetry.addLine("Using stationary shot calculation");
        return goalPos().subtract(cannonPos()).toVector2D();
    }

    private Position2D cannonPos() {
        return robotPosition.getPose().addRelative(CannonConfig.CANNON_RELATIVE_POSITION);
    }

    private Position2D goalPos() {
        return PlayingField.goalPos(team);
    }
}
