package math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

import java.util.Locale;

public class Position2D {
    private final double x;
    private final double y;
    private final DistanceUnit unit;

    /**
     * Creates a new Position2D object.
     *
     * @param unit the unit of distance for both x and y
     * @param x the x position
     * @param y the y position
     */
    public Position2D(DistanceUnit unit, double x, double y) {
        this.x = x;
        this.y = y;
        this.unit = unit;
    }

    /**
     * Creates a new Position2D object from a Pose2D by extracting its x and y values
     *
     * @param pose the Pose2D object to create the Position2D from
     */
    public Position2D(Pose2D pose) {
        this.unit = DistanceUnit.MM;
        this.x = pose.getX(unit);
        this.y = pose.getY(unit);
    }

    /**
     * Creates a new Position2D object from two Distance objects by extracting their values and
     * ensuring they are converted to the same unit.
     *
     * @param x the Distance object representing the x position
     * @param y the Distance object representing the y position
     */
    public Position2D(Distance x, Distance y) {
        this(DistanceUnit.MM, x.toMillimeters(), y.toMillimeters());
    }

    /** Creates a new default Position2D object at (0, 0) */
    public Position2D() {
        this(DistanceUnit.MM, 0, 0);
    }

    /**
     * Converts a Position object to a Position2D object by extracting its x and y values
     *
     * @param position the Position object to convert
     * @return the resulting Position2D object
     */
    public static Position2D fromPosition(Position position) {
        return new Position2D(position.unit, position.x, position.y);
    }

    /**
     * Gets x in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the x member converted to the desired distance unit
     */
    public double getX(DistanceUnit unit) {
        return unit.fromUnit(this.unit, x);
    }

    /**
     * Gets x as a Distance object in the unit of this Position2D
     *
     * @return the x member as a Distance object
     */
    public Distance getX() {
        return new Distance(unit, x);
    }

    /**
     * Gets y in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return y member converted to the desired distance unit
     */
    public double getY(DistanceUnit unit) {
        return unit.fromUnit(this.unit, y);
    }

    /**
     * Gets y as a Distance object in the unit of this Position2D
     *
     * @return the y member as a Distance object
     */
    public Distance getY() {
        return new Distance(unit, y);
    }

    /**
     * Adds another Position2D to this Position2D by adding their x and y values and returns a new
     * Position2D with the resulting values.
     *
     * @param other the Position2D to add to this Position2D
     * @return a new Position2D that is the result of adding the other Position2D to this one
     */
    public Position2D add(Position2D other) {
        Distance newX = getX().add(other.getX());
        Distance newY = getY().add(other.getY());
        return new Position2D(newX, newY);
    }

    /**
     * Subtracts another Position2D from this Position2D by subtracting their x and y values and
     * returns a new Position2D with the resulting values.
     *
     * @param other the Position2D to subtract from this Position2D
     * @return a new Position2D that is the result of subtracting the other Position2D from this one
     */
    public Position2D subtract(Position2D other) {
        Distance newX = getX().subtract(other.getX());
        Distance newY = getY().subtract(other.getY());
        return new Position2D(newX, newY);
    }

    /**
     * Converts this Position2D to an Angle object by calculating the angle from the origin (0, 0)
     * to the point represented by this Position2D using the atan2 function.
     *
     * @return an Angle object representing the angle from the origin to this Position2D
     */
    public Angle direction() {
        double rads = Math.atan2(getY().toMillimeters(), getX().toMillimeters());
        return Angle.fromRadians(rads);
    }

    /**
     * Calculates the distance from the origin (0, 0) to the point represented by this Position2D
     * using the Pythagorean theorem. The result is always non-negative.
     *
     * @return a Distance object representing the distance from the origin to this Position2D
     */
    public Distance hypot() {
        double dist = Math.hypot(getX().toMillimeters(), getY().toMillimeters());
        return Distance.fromMillimeters(dist);
    }

    /**
     * Calculates the distance from this Position2D to another Position2D by using the Pythagorean
     * theorem. The result is always non-negative.
     *
     * @param other the Position2D to calculate the distance to
     * @return a Distance object representing the distance from this Position2D to the other
     */
    public Distance distanceTo(Position2D other) {
        return subtract(other).hypot();
    }

    /**
     * Returns a string representation of the object in a human readable format for debugging
     * purposes.
     *
     * @return a string representation of the object
     */
    @NonNull
    public String toString() {
        return String.format(
                Locale.ENGLISH,
                "(Position2D) x=%.3f %s, y=%.3f %s",
                getX(DistanceUnit.METER),
                DistanceUnit.METER,
                getY(DistanceUnit.METER),
                DistanceUnit.METER);
    }
}
