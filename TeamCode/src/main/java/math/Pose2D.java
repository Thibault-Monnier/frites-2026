package math;

import androidx.annotation.NonNull;

import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

/** Pose2D represents the position and heading of an object in 2D space. */
public class Pose2D {
    private final double x;
    private final double y;
    private final DistanceUnit distanceUnit;
    private final double heading;
    private final AngleUnit headingUnit;

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
     * Creates a new Pose2D object from two Distance objects and an Angle object.
     *
     * @param x the x position as a Distance object
     * @param y the y position as a Distance object
     * @param heading the heading as an Angle object
     */
    public Pose2D(Distance x, Distance y, Angle heading) {
        this(
                DistanceUnit.MM,
                x.getValue(DistanceUnit.MM),
                y.getValue(DistanceUnit.MM),
                AngleUnit.RADIANS,
                heading.getValue(AngleUnit.RADIANS));
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
     * This gets X as a Distance object
     *
     * @return the X member as a Distance object
     */
    public Distance getX() {
        return new Distance(distanceUnit, x);
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
     * This gets Y as a Distance object
     *
     * @return the Y member as a Distance object
     */
    public Distance getY() {
        return new Distance(distanceUnit, y);
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
     * This gets the heading as an Angle object
     *
     * @return the heading as an Angle object
     */
    public Angle getHeading() {
        return new Angle(headingUnit, heading);
    }

    /**
     * This checks if any of the members are NaN
     *
     * @return true if any member is NaN, false otherwise
     */
    public boolean hasNaN() {
        return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(heading);
    }

    /**
     * This adds another Pose2D to this Pose2D and returns the result as a new Pose2D.
     *
     * @param other the other Pose2D to add
     * @return a new Pose2D that is the sum of this Pose2D and the other Pose2D
     */
    public Pose2D add(Pose2D other) {
        return new Pose2D(
                getX().add(other.getX()),
                getY().add(other.getY()),
                getHeading().add(other.getHeading()));
    }

    /**
     * This adds x, y, and heading to this Pose2D and returns the result as a new Pose2D.
     *
     * @param x the x to add as a Distance object
     * @param y the y to add as a Distance object
     * @param heading the heading to add as an Angle object
     * @return a new Pose2D that is the sum of this Pose2D and the given x, y, and heading
     */
    public Pose2D add(Distance x, Distance y, Angle heading) {
        return add(new Pose2D(x, y, heading));
    }

    /**
     * This subtracts another Pose2D from this Pose2D and returns the result as a new Pose2D.
     *
     * @param other the other Pose2D to subtract
     * @return a new Pose2D that is the difference of this Pose2D and the other Pose2D
     */
    public Pose2D subtract(Pose2D other) {
        return new Pose2D(
                getX().subtract(other.getX()),
                getY().subtract(other.getY()),
                getHeading().subtract(other.getHeading()));
    }

    /**
     * Converts a local displacement into world coordinates and adds it to this pose's position.
     *
     * @param other the Vector2D displacement relative to this pose
     * @return the resulting absolute Position2D
     */
    public Position2D addRelative(Vector2D other) {
        Vector2D rotatedOther = other.rotate(getHeading());
        return toPosition2D().add(rotatedOther);
    }

    /**
     * This returns a string representation of the object in a human readable format for debugging
     * purposes.
     *
     * @return a string representation of the object
     */
    @NonNull
    public String toString() {
        return String.format(
                Locale.ENGLISH, "(Pose2D) x=%s, y=%s, heading=%s", getX(), getY(), getHeading());
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
     * Converts this Pose2D to a PedroCoordinates Pose, changing the coordinate system accordingly.
     *
     * @return a new PedroCoordinates Pose representing the same location as this Pose2D
     */
    public Pose toPedropathingPose() {
        Pose pose =
                new Pose(
                        getX(DistanceUnit.INCH),
                        getY(DistanceUnit.INCH),
                        getHeading(AngleUnit.RADIANS),
                        FTCCoordinates.INSTANCE);
        return pose.getAsCoordinateSystem(PedroCoordinates.INSTANCE);
    }

    /**
     * Converts this Pose2D to a Position2D object.
     *
     * @return a new Position2D object with the same x and y values as this Pose2D
     */
    public Position2D toPosition2D() {
        return new Position2D(distanceUnit, getX(distanceUnit), getY(distanceUnit));
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

    /** Converts a PedroCoordinates Pose to a Pose2D, changing the coordinate system accordingly. */
    public static Pose2D fromPedropathingPose(Pose pose) {
        Pose ftcPose = pose.getAsCoordinateSystem(FTCCoordinates.INSTANCE);
        return new Pose2D(
                DistanceUnit.INCH,
                ftcPose.getX(),
                ftcPose.getY(),
                AngleUnit.RADIANS,
                ftcPose.getHeading());
    }
}
