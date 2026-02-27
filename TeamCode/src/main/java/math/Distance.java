package math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

public class Distance {
    private final double value;
    private final DistanceUnit unit;

    /**
     * Creates a new Distance object.
     *
     * @param unit the distance unit of the value
     * @param value the distance value
     */
    public Distance(DistanceUnit unit, double value) {
        this.value = value;
        this.unit = unit;
    }

    /** Creates a new default Distance object at 0 */
    public Distance() {
        this(DistanceUnit.MM, 0);
    }

    /** Creates a new Distance object from millimeters input */
    public static Distance fromMillimeters(double millimeters) {
        return new Distance(DistanceUnit.MM, millimeters);
    }

    /** Creates a new Distance object from meters input */
    public static Distance fromCentimeters(double centimeters) {
        return new Distance(DistanceUnit.CM, centimeters);
    }

    /** Creates a new Distance object from meters input */
    public static Distance fromMeters(double meters) {
        return new Distance(DistanceUnit.METER, meters);
    }

    /** Creates a new Distance object from inches input */
    public static Distance fromInches(double inches) {
        return new Distance(DistanceUnit.INCH, inches);
    }

    /**
     * Gets the distance value in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the value member converted to the desired distance unit
     */
    public double getValue(DistanceUnit unit) {
        return unit.fromUnit(this.unit, value);
    }

    /** Gets the distance value converted to millimeters */
    public double toMillimeters() {
        return getValue(DistanceUnit.MM);
    }

    /** Gets the distance value converted to inches */
    public double toInches() {
        return getValue(DistanceUnit.INCH);
    }

    /** Gets the distance value converted to meters */
    public double toMeters() {
        return getValue(DistanceUnit.METER);
    }

    /** Check if this Distance is zero. */
    public boolean isZero() {
        return value == 0;
    }

    /** Negates this Distance and returns the result as a new Distance object. */
    public Distance negate() {
        return new Distance(unit, -value);
    }

    /** Adds another Distance to this Distance and returns the result as a new Distance object. */
    public Distance add(Distance other) {
        double sumInMM = toMillimeters() + other.toMillimeters();
        return Distance.fromMillimeters(sumInMM);
    }

    /**
     * Subtracts another Distance from this Distance and returns the result as a new Distance
     * object.
     */
    public Distance subtract(Distance other) {
        double differenceInMM = toMillimeters() - other.toMillimeters();
        return Distance.fromMillimeters(differenceInMM);
    }

    /** Multiplies this Distance by a scalar and returns the result as a new Distance object. */
    public Distance multiply(double scalar) {
        double productInMM = toMillimeters() * scalar;
        return Distance.fromMillimeters(productInMM);
    }

    /** Divides this Distance by a scalar and returns the result as a new Distance object. */
    public Distance divide(double scalar) {
        double quotientInMM = toMillimeters() / scalar;
        return Distance.fromMillimeters(quotientInMM);
    }

    /**
     * Divides this Distance by another Distance and returns the result as a new Distance object.
     */
    public Distance divide(Distance other) {
        double quotient = toMillimeters() / other.toMillimeters();
        return Distance.fromMillimeters(quotient);
    }

    /** Halves this Distance and returns the result as a new Distance object. */
    public Distance halve() {
        return divide(2);
    }

    /** Divides this Distance by another Distance and returns the result as a unitless ratio. */
    public double ratio(Distance other) {
        return toMillimeters() / other.toMillimeters();
    }

    /** Whether this Distance is greater or equal to another Distance. */
    public boolean geq(Distance other) {
        return toMillimeters() >= other.toMillimeters();
    }

    /** Whether this Distance is less than or equal to another Distance. */
    public boolean leq(Distance other) {
        return toMillimeters() <= other.toMillimeters();
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
                Locale.ENGLISH, "%.3f %s", getValue(DistanceUnit.METER), DistanceUnit.METER);
    }
}
