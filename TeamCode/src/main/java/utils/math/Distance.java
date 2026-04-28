package utils.math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

public class Distance {
    private final double value;
    private final DistanceUnit unit;

    public Distance(DistanceUnit unit, double value) {
        this.value = value;
        this.unit = unit;
    }

    public Distance() {
        this(DistanceUnit.MM, 0);
    }

    public static Distance fromMillimeters(double millimeters) {
        return new Distance(DistanceUnit.MM, millimeters);
    }

    public static Distance fromCentimeters(double centimeters) {
        return new Distance(DistanceUnit.CM, centimeters);
    }

    public static Distance fromMeters(double meters) {
        return new Distance(DistanceUnit.METER, meters);
    }

    public static Distance fromInches(double inches) {
        return new Distance(DistanceUnit.INCH, inches);
    }

    /// The distance value converted to the desired distance unit
    public double getValue(DistanceUnit unit) {
        return unit.fromUnit(this.unit, value);
    }

    public double toMillimeters() {
        return getValue(DistanceUnit.MM);
    }

    public double toInches() {
        return getValue(DistanceUnit.INCH);
    }

    public double toMeters() {
        return getValue(DistanceUnit.METER);
    }

    public boolean isZero() {
        return value == 0;
    }

    /// The inverse of this Distance
    public Distance negate() {
        return new Distance(unit, -value);
    }

    public Distance add(Distance other) {
        double sumInMM = toMillimeters() + other.toMillimeters();
        return Distance.fromMillimeters(sumInMM);
    }

    public Distance subtract(Distance other) {
        double differenceInMM = toMillimeters() - other.toMillimeters();
        return Distance.fromMillimeters(differenceInMM);
    }

    /// Multiplies this Distance by a scalar
    public Distance multiply(double scalar) {
        double productInMM = toMillimeters() * scalar;
        return Distance.fromMillimeters(productInMM);
    }

    /// Divides this Distance by a scalar
    public Distance divide(double scalar) {
        double quotientInMM = toMillimeters() / scalar;
        return Distance.fromMillimeters(quotientInMM);
    }

    public Distance divide(Distance other) {
        double quotient = toMillimeters() / other.toMillimeters();
        return Distance.fromMillimeters(quotient);
    }

    /// The half of this Distance
    public Distance halve() {
        return divide(2);
    }

    /// Divides this Distance by another Distance and returns the result as a unitless ratio
    public double ratio(Distance other) {
        return toMillimeters() / other.toMillimeters();
    }

    /// Whether this Distance is greater or equal to another Distance.
    public boolean geq(Distance other) {
        return toMillimeters() >= other.toMillimeters();
    }

    /// Whether this Distance is less than or equal to another Distance.
    public boolean leq(Distance other) {
        return toMillimeters() <= other.toMillimeters();
    }

    @NonNull
    public String toString() {
        return String.format(
                Locale.ENGLISH, "%.3f %s", getValue(DistanceUnit.METER), DistanceUnit.METER);
    }
}
