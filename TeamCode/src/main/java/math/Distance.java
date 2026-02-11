package math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
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

    /** Creates a new Distance object from inches input */
    public static Distance fromInches(double inches) {
        return new Distance(DistanceUnit.INCH, inches);
    }

    /** Adds another Distance to this Distance and returns the result as a new Distance object. */
    public Distance add(Distance other) {
        double sumInMM = this.toMillimeters() + other.toMillimeters();
        return Distance.fromMillimeters(sumInMM);
    }

    /**
     * Subtracts another Distance from this Distance and returns the result as a new Distance
     * object.
     */
    public Distance subtract(Distance other) {
        double differenceInMM = this.toMillimeters() - other.toMillimeters();
        return Distance.fromMillimeters(differenceInMM);
    }

    /** Calculates the hypotenuse of a right triangle given the two legs as Distance objects. */
    public static Distance hypot(Distance a, Distance b) {
        double hypotenuseInMM = Math.hypot(a.toMillimeters(), b.toMillimeters());
        return Distance.fromMillimeters(hypotenuseInMM);
    }

    public static Distance hypot(Position2D a, Position2D b) {
        return hypot(a.getX().subtract(b.getX()), a.getY().subtract(b.getY()));
    }

    /**
     * Calculates the angle of a right triangle given the opposite and adjacent legs as Distance
     * objects.
     */
    public static Angle atan2(Distance y, Distance x) {
        double angleInRadians = Math.atan2(y.toMillimeters(), x.toMillimeters());
        return new Angle(AngleUnit.RADIANS, angleInRadians);
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
