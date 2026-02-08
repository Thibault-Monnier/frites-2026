package math;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

import java.util.Locale;

public class Position2D {
    private final double x;
    private final double y;
    private final DistanceUnit unit;

    /**
     * Creates a new Position2D object.
     *
     * @param unit the unit of distance for both x and y
     * @param x the x position
     * @param y the y position
     */
    public Position2D(DistanceUnit unit, double x, double y) {
        this.x = x;
        this.y = y;
        this.unit = unit;
    }

    /**
     * Creates a new Position2D object from a Pose2D by extracting its x and y values
     *
     * @param pose the Pose2D object to create the Position2D from
     */
    public Position2D(Pose2D pose) {
        this.unit = DistanceUnit.MM;
        this.x = pose.getX(unit);
        this.y = pose.getY(unit);
    }

    /**
     * Creates a new Position2D object from two Distance objects by extracting their values and
     * ensuring they are converted to the same unit.
     *
     * @param x the Distance object representing the x position
     * @param y the Distance object representing the y position
     */
    public Position2D(Distance x, Distance y) {
        this(DistanceUnit.MM, x.toMillimeters(), y.toMillimeters());
    }

    /** Creates a new default Position2D object at (0, 0) */
    public Position2D() {
        this(DistanceUnit.MM, 0, 0);
    }

    /**
     * Gets x in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the x member converted to the desired distance unit
     */
    public double getX(DistanceUnit unit) {
        return unit.fromUnit(this.unit, x);
    }

    /**
     * Gets x as a Distance object in the unit of this Position2D
     *
     * @return the x member as a Distance object
     */
    public Distance getX() {
        return new Distance(unit, x);
    }

    /**
     * Gets y in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return y member converted to the desired distance unit
     */
    public double getY(DistanceUnit unit) {
        return unit.fromUnit(this.unit, y);
    }

    /**
     * Gets y as a Distance object in the unit of this Position2D
     *
     * @return the y member as a Distance object
     */
    public Distance getY() {
        return new Distance(unit, y);
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
                Locale.ENGLISH,
                "(Position2D) x=%.3f %s, y=%.3f %s",
                getX(DistanceUnit.METER),
                DistanceUnit.METER,
                getY(DistanceUnit.METER),
                DistanceUnit.METER);
    }

    /**
     * Converts a Position object to a Position2D object by extracting its x and y values
     *
     * @param position the Position object to convert
     * @return the resulting Position2D object
     */
    public static Position2D fromPosition(Position position) {
        return new Position2D(position.unit, position.x, position.y);
    }
}
