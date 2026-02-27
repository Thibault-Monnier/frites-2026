package math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Locale;

public class Angle {
    private final double value;
    private final AngleUnit unit;

    /**
     * Creates a new Angle object.
     *
     * @param unit the angle unit of the value
     * @param value the angle value
     */
    public Angle(AngleUnit unit, double value) {
        this.value = unit.normalize(value);
        this.unit = unit;
    }

    /** Creates a new default Angle object with 0 degrees. */
    public Angle() {
        this(AngleUnit.DEGREES, 0);
    }

    /** Creates a new Angle object from degrees input */
    public static Angle fromDegrees(double degrees) {
        return new Angle(AngleUnit.DEGREES, degrees);
    }

    /** Creates a new Angle object from radians input */
    public static Angle fromRadians(double radians) {
        return new Angle(AngleUnit.RADIANS, radians);
    }

    /**
     * Gets the angle value in the desired angle unit
     *
     * @param unit the desired angle unit
     * @return the value member converted to the desired angle unit
     */
    public double getValue(AngleUnit unit) {
        return unit.fromUnit(this.unit, value);
    }

    /** Gets the angle value converted to degrees */
    public double toDegrees() {
        return getValue(AngleUnit.DEGREES);
    }

    /** Gets the angle value converted to radians */
    public double toRadians() {
        return getValue(AngleUnit.RADIANS);
    }

    /** Negates this Angle and returns the result as a new Angle object. */
    public Angle negate() {
        return fromRadians(-toRadians());
    }

    /** Adds another Angle to this Angle and returns the result as a new Angle object. */
    public Angle add(Angle other) {
        double sumInRadians = toRadians() + other.toRadians();
        return Angle.fromRadians(sumInRadians);
    }

    /** Subtracts another Angle from this Angle and returns the result as a new Angle object. */
    public Angle subtract(Angle other) {
        double differenceInRadians = toRadians() - other.toRadians();
        return Angle.fromRadians(differenceInRadians);
    }

    /** Multiplies this Angle by a scalar and returns the result as a new Angle object. */
    public Angle multiply(double scalar) {
        double productInRadians = toRadians() * scalar;
        return Angle.fromRadians(productInRadians);
    }

    /** Divides this Angle by another Angle and returns the result as a unitless ratio. */
    public double ratio(Angle other) {
        return toRadians() / other.toRadians();
    }

    /** Returns the sine of this Angle. */
    public double sin() {
        return Math.sin(toRadians());
    }

    /** Returns the cosine of this Angle. */
    public double cos() {
        return Math.cos(toRadians());
    }

    /** Returns the tangent of this Angle. */
    public double tan() {
        return Math.tan(toRadians());
    }

    /** Returns the absolute value of this Angle. */
    public Angle abs() {
        return fromRadians(Math.abs(toRadians()));
    }

    /** Returns whether this Angle is less than or equal to another Angle */
    public boolean leq(Angle other) {
        return toRadians() <= other.toRadians();
    }

    /**
     * Returns a string representation of the object in a human readable format for debugging
     * purposes.
     *
     * @return a string representation of the Angle object
     */
    @NonNull
    public String toString() {
        return String.format(
                Locale.ENGLISH, "%.2f %s", getValue(AngleUnit.DEGREES), AngleUnit.DEGREES);
    }
}
