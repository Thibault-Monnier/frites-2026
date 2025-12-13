package core.math;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

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

    /** Creates a new default Pose2D object at (0, 0) with 0 heading */
    public Pose2D() {
        this(DistanceUnit.MM, 0, 0, AngleUnit.RADIANS, 0);
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
     * This returns a string representation of the object in a human readable format for debugging
     * purposes.
     *
     * @return a string representation of the object
     */
    @NonNull
    @Override
    public String toString() {
        return String.format(
                Locale.ENGLISH,
                "(Pose2D) x=%.3f %s, y=%.3f %s, heading=%.2f %s",
                getX(DistanceUnit.METER),
                DistanceUnit.METER,
                getY(DistanceUnit.METER),
                DistanceUnit.METER,
                getHeading(AngleUnit.DEGREES),
                AngleUnit.DEGREES);
    }

    /**
     * Converts this Pose2D to a Roadrunner Pose2d object.
     *
     * @param distanceUnit the unit to convert x and y into
     * @param angleUnit the unit to convert heading into
     * @return a new Roadrunner Pose2d with x, y and heading converted to the desired units
     */
    public Pose2d toPose2d(DistanceUnit distanceUnit, AngleUnit angleUnit) {
        return new Pose2d(getX(distanceUnit), getY(distanceUnit), getHeading(angleUnit));
    }

    /**
     * Converts a Roadrunner Pose2d object to a Pose2D object.
     *
     * @param pose2d the Roadrunner Pose2d object to convert
     * @param distanceUnit the distance unit used in pose2d
     * @param angleUnit the angle unit used in pose2d
     * @return a new Pose2D object with x, y and heading from the Pose2d
     */
    public static Pose2D fromPose2d(Pose2d pose2d, DistanceUnit distanceUnit, AngleUnit angleUnit) {
        return new Pose2D(
                distanceUnit,
                pose2d.position.x,
                pose2d.position.y,
                angleUnit,
                pose2d.heading.toDouble());
    }

    /**
     * Converts this Pose2D to an FTC navigation Pose2D object.
     *
     * @return a new FTC navigation Pose2D with the same values as this Pose2D
     */
    public org.firstinspires.ftc.robotcore.external.navigation.Pose2D toNavigationPose2D() {
        return new org.firstinspires.ftc.robotcore.external.navigation.Pose2D(
                DistanceUnit.MM,
                getX(DistanceUnit.MM),
                getY(DistanceUnit.MM),
                AngleUnit.RADIANS,
                getHeading(AngleUnit.RADIANS));
    }

    /**
     * Converts an FTC navigation Pose2D object to a Pose2D object.
     *
     * @param navPose the FTC navigation Pose2D object to convert
     * @return a new Pose2D object the same values as the navPose
     */
    public static Pose2D fromNavigationPose2D(
            org.firstinspires.ftc.robotcore.external.navigation.Pose2D navPose) {
        return new Pose2D(
                DistanceUnit.MM,
                navPose.getX(DistanceUnit.MM),
                navPose.getY(DistanceUnit.MM),
                AngleUnit.RADIANS,
                navPose.getHeading(AngleUnit.RADIANS));
    }
}
