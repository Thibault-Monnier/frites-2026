package core.math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/** Pose2D represents the position and heading of an object in 2D space. */
public class Pose2D {
    protected final double x;
    protected final double y;
    protected final DistanceUnit distanceUnit;
    protected final double heading;
    protected final AngleUnit headingUnit;

    /**
     * Creates a new Pose2D object.
     *
     * @param distanceUnit the unit of distance for both x and y
     * @param x the x position
     * @param y the y position
     * @param headingUnit the unit of heading
     * @param heading the heading
     */
    public Pose2D(
            DistanceUnit distanceUnit, double x, double y, AngleUnit headingUnit, double heading) {
        this.x = x;
        this.y = y;
        this.distanceUnit = distanceUnit;
        this.heading = heading;
        this.headingUnit = headingUnit;
    }

    /**
     * This gets X in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the X member converted to the desired distance unit
     */
    public double getX(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, x);
    }

    /**
     * This gets the Y in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the Y member converted to the desired distance unit
     */
    public double getY(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, y);
    }

    /**
     * This gets the heading in the desired distance unit Be aware that this normalizes the angle to
     * be between -PI and PI for RADIANS or between -180 and 180 for DEGREES
     *
     * @param unit the desired distance unit
     * @return the heading converted to the desired Angle Unit
     */
    public double getHeading(AngleUnit unit) {
        return unit.fromUnit(this.headingUnit, heading);
    }

    /**
     * This adds two Pose2D objects together and returns a new Pose2D object as the result. The x
     * and y values are added together, and the headings are added together and normalized.
     *
     * @param other the other Pose2D object to add
     * @return a new Pose2D object that is the result of adding the two Pose2D objects together
     */
    public Pose2D add(Pose2D other) {
        double newX = this.getX(DistanceUnit.MM) + other.getX(DistanceUnit.MM);
        double newY = this.getY(DistanceUnit.MM) + other.getY(DistanceUnit.MM);
        double newHeading =
                AngleUnit.normalizeRadians(
                        this.getHeading(AngleUnit.RADIANS) + other.getHeading(AngleUnit.RADIANS));
        return new Pose2D(DistanceUnit.MM, newX, newY, AngleUnit.RADIANS, newHeading);
    }

    /**
     * This returns a string representation of the object in a human readable format for debugging
     * purposes.
     *
     * @return a string representation of the object
     */
    @NonNull
    @Override
    public String toString() {
        return "(Pose2D) x="
                + x
                + ", y="
                + y
                + " "
                + distanceUnit
                + ", heading="
                + heading
                + " "
                + headingUnit;
    }
}
