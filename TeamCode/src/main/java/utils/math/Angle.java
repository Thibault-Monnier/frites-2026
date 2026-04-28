package utils.math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Locale;

public class Angle {
    private final double value;
    private final AngleUnit unit;

    public Angle(AngleUnit unit, double value) {
        this.value = unit.normalize(value);
        this.unit = unit;
    }

    public Angle() {
        this(AngleUnit.DEGREES, 0);
    }

    public static Angle fromDegrees(double degrees) {
        return new Angle(AngleUnit.DEGREES, degrees);
    }

    public static Angle fromRadians(double radians) {
        return new Angle(AngleUnit.RADIANS, radians);
    }

    /// The angle value converted to the desired angle unit
    public double getValue(AngleUnit unit) {
        return unit.fromUnit(this.unit, value);
    }

    /// The angle value in degrees
    public double toDegrees() {
        return getValue(AngleUnit.DEGREES);
    }

    /// The angle value in radians
    public double toRadians() {
        return getValue(AngleUnit.RADIANS);
    }

    public boolean isNaN() {
        return Double.isNaN(value);
    }

    /// The inverse of this Angle
    public Angle negate() {
        return fromRadians(-toRadians());
    }

    public Angle add(Angle other) {
        double sumInRadians = toRadians() + other.toRadians();
        return Angle.fromRadians(sumInRadians);
    }

    public Angle subtract(Angle other) {
        double differenceInRadians = toRadians() - other.toRadians();
        return Angle.fromRadians(differenceInRadians);
    }

    /// Multiplies this Angle by a scalar
    public Angle multiply(double scalar) {
        double productInRadians = toRadians() * scalar;
        return Angle.fromRadians(productInRadians);
    }

    /// Divides this Angle by another Angle and returns the result as a unitless ratio
    public double ratio(Angle other) {
        return toRadians() / other.toRadians();
    }

    /// The sine of this Angle
    public double sin() {
        return Math.sin(toRadians());
    }

    /// The cosine of this Angle
    public double cos() {
        return Math.cos(toRadians());
    }

    /// The tangent of this Angle
    public double tan() {
        return Math.tan(toRadians());
    }

    /// The absolute value of this Angle
    public Angle abs() {
        return fromRadians(Math.abs(toRadians()));
    }

    /// Whether this Angle is less than or equal to another Angle
    public boolean leq(Angle other) {
        return toRadians() <= other.toRadians();
    }

    @NonNull
    public String toString() {
        return String.format(
                Locale.ENGLISH, "%.2f %s", getValue(AngleUnit.DEGREES), AngleUnit.DEGREES);
    }
}
