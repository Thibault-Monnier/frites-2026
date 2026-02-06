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
        this.value = value;
        this.unit = unit;
    }

    /** Creates a new default Angle object at 0 */
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

    /** Adds another Angle to this Angle and returns the result as a new Angle object. */
    public Angle add(Angle other) {
        double otherValueInThisUnit = other.getValue(this.unit);
        return new Angle(this.unit, this.value + otherValueInThisUnit);
    }

    /** Subtracts another Angle from this Angle and returns the result as a new Angle object. */
    public Angle subtract(Angle other) {
        double otherValueInThisUnit = other.getValue(this.unit);
        return new Angle(this.unit, this.value - otherValueInThisUnit);
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
                Locale.ENGLISH, "(Angle) %.2f %s", getValue(AngleUnit.DEGREES), AngleUnit.DEGREES);
    }
}
