package utils.math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

/// Represents a 2D vector, which is a displacement between two positions.
public class Vector2D {
    private final double x;
    private final double y;
    private final DistanceUnit distanceUnit;

    public Vector2D(DistanceUnit distanceUnit, double x, double y) {
        this.x = x;
        this.y = y;
        this.distanceUnit = distanceUnit;
    }

    public Vector2D() {
        this(DistanceUnit.MM, 0, 0);
    }

    public Vector2D(Distance x, Distance y) {
        this(DistanceUnit.MM, x.getValue(DistanceUnit.MM), y.getValue(DistanceUnit.MM));
    }

    public Vector2D(Distance magnitude, Angle direction) {
        this(magnitude.multiply(direction.cos()), magnitude.multiply(direction.sin()));
    }

    /// The x component converted to the desired distance unit
    public double getX(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, x);
    }

    /// The x component as a Distance object
    public Distance getX() {
        return new Distance(distanceUnit, x);
    }

    /// The y component converted to the desired distance unit
    public double getY(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, y);
    }

    /// The y component as a Distance object
    public Distance getY() {
        return new Distance(distanceUnit, y);
    }

    public Distance magnitude() {
        return new Distance(distanceUnit, Math.hypot(x, y));
    }

    public Angle direction() {
        double rads = Math.atan2(getY(distanceUnit), getX(distanceUnit));
        return Angle.fromRadians(rads);
    }

    /** Normalizes the vector to have a magnitude of 1 while maintaining its direction. */
    public Vector2D normalize() {
        Distance mag = magnitude();
        if (mag.isZero()) {
            return this;
        }
        return new Vector2D(getX().divide(mag), getY().divide(mag));
    }

    /**
     * Normalizes the vector by dividing both components by the maximum absolute value of the
     * components, ensuring that the vector scales to fit within a unit square while maintaining its
     * direction.
     */
    public Vector2D normalizeMax() {
        double max = Math.max(Math.abs(x), Math.abs(y));
        if (max == 0) {
            return this;
        }
        return new Vector2D(distanceUnit, x / max, y / max);
    }

    public Vector2D scale(double scalar) {
        return new Vector2D(distanceUnit, x * scalar, y * scalar);
    }

    /// Rotates the vector by the given angle
    public Vector2D rotate(Angle angle) {
        return new Vector2D(magnitude(), direction().add(angle));
    }

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "(Vector2D) x=%s, y=%s", getX(), getY());
    }
}
