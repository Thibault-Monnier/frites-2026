package math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

/**
 * Vector2D represents a vector in 2D space. It has an x and y component, as well as a unit of
 * distance.
 */
public class Vector2D {
    private final double x;
    private final double y;
    private final DistanceUnit distanceUnit;

    /**
     * Creates a new Vector2D object.
     *
     * @param distanceUnit the unit of distance for both x and y
     * @param x the x component of the vector
     * @param y the y component of the vector
     */
    public Vector2D(DistanceUnit distanceUnit, double x, double y) {
        this.x = x;
        this.y = y;
        this.distanceUnit = distanceUnit;
    }

    /** Creates a new default Vector2D object with 0 magnitude. */
    public Vector2D() {
        this(DistanceUnit.MM, 0, 0);
    }

    /**
     * Creates a new Vector2D object from two Distance objects.
     *
     * @param x the x component of the vector as a Distance object
     * @param y the y component of the vector as a Distance object
     */
    public Vector2D(Distance x, Distance y) {
        this(DistanceUnit.MM, x.getValue(DistanceUnit.MM), y.getValue(DistanceUnit.MM));
    }

    /**
     * Creates a new Vector2D object from a magnitude and direction.
     *
     * @param magnitude the magnitude of the vector as a Distance object
     * @param direction the direction of the vector as an Angle object
     */
    public Vector2D(Distance magnitude, Angle direction) {
        this(magnitude.multiply(direction.cos()), magnitude.multiply(direction.sin()));
    }

    /**
     * Creates a new Vector2D object from a Position2D object.
     *
     * @param position the Position2D object to create the Vector2D from
     */
    public Vector2D(Position2D position) {
        this(position.getX(), position.getY());
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
     * This gets the raw X value without converting to the desired distance unit. This is useful for
     * calculations that require the original values, such as normalization.
     *
     * @return the raw X value
     */
    public double getRawX() {
        return x;
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
     * This gets the raw Y value without converting to the desired distance unit. This is useful for
     * calculations that require the original values, such as normalization.
     *
     * @return the raw Y value
     */
    public double getRawY() {
        return y;
    }

    /**
     * This gets the magnitude of the vector in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the magnitude of the vector converted to the desired distance unit
     */
    public double getMagnitude(DistanceUnit unit) {
        return Math.sqrt(Math.pow(getX(unit), 2) + Math.pow(getY(unit), 2));
    }

    /**
     * This normalizes the vector to have a magnitude of 1, while maintaining its direction. The
     * distance unit of the resulting vector is the same as the original vector.
     *
     * @return a new Vector2D object that is the normalized version of this vector
     */
    public Vector2D normalize() {
        double magnitude = getMagnitude(distanceUnit);
        if (magnitude == 0) {
            return this;
        }
        return new Vector2D(distanceUnit, x / magnitude, y / magnitude);
    }

    /**
     * This normalizes the vector by dividing both components by the maximum absolute value of the
     * components, ensuring that the vector scales to fit within a unit square while maintaining its
     * direction.
     *
     * @return a new Vector2D object that is the normalized version of this vector
     */
    public Vector2D normalizeMax() {
        double max = Math.max(Math.abs(x), Math.abs(y));
        if (max == 0) {
            return this;
        }
        return new Vector2D(distanceUnit, x / max, y / max);
    }

    /**
     * This scales the vector by a scalar, multiplying both the x and y components by the scalar.
     *
     * @param scalar the value to scale the vector by
     * @return a new Vector2D object that is the scaled version of this vector
     */
    public Vector2D scale(double scalar) {
        return new Vector2D(distanceUnit, x * scalar, y * scalar);
    }

    /**
     * This returns a string representation of the vector in human readable format for debugging
     * purposes.
     */
    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "(Vector2D) x=%s, y=%s", getX(), getY());
    }
}
