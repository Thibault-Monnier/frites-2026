package math;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

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
            return new Vector2D(distanceUnit, 0, 0);
        }
        return new Vector2D(distanceUnit, x / magnitude, y / magnitude);
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
}
