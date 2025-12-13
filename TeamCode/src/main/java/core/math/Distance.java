package core.math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

public class Distance {
    protected final double value;
    protected final DistanceUnit unit;

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

    /**
     * Returns a string representation of the object in a human readable format for debugging
     * purposes.
     *
     * @return a string representation of the object
     */
    @NonNull
    @Override
    public String toString() {
        return String.format(
                Locale.ENGLISH,
                "(Distance) %.3f %s",
                getValue(DistanceUnit.METER),
                DistanceUnit.METER);
    }
}
