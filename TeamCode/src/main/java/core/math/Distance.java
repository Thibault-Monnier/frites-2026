package core.math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

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

    /** Creates a new Distance object at 0 */
    public Distance() {
        this(DistanceUnit.MM, 0);
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

    /**
     * Returns a string representation of the object in a human readable format for debugging
     * purposes.
     *
     * @return a string representation of the object
     */
    @NonNull
    @Override
    public String toString() {
        return "(Distance) value=" + value + " " + unit;
    }
}
