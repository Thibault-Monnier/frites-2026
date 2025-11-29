package core.math;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Position2D {
    protected final double x;
    protected final double y;
    protected final DistanceUnit unit;

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
     * Creates a new Position2D object from a Vector2d
     *
     * @param unit the unit of distance for both x and y
     * @param vec the vector containing x and y
     */
    public Position2D(DistanceUnit unit, Vector2d vec) {
        this(unit, vec.x, vec.y);
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

    /** Creates a new Position2D object at (0, 0) */
    public Position2D() {
        this(DistanceUnit.MM, 0, 0);
    }

    /**
     * Gets X in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the X member converted to the desired distance unit
     */
    public double getX(DistanceUnit unit) {
        return unit.fromUnit(this.unit, x);
    }

    /**
     * Gets the Y in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the Y member converted to the desired distance unit
     */
    public double getY(DistanceUnit unit) {
        return unit.fromUnit(this.unit, y);
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
        return "(Pose2D) x=" + x + ", y=" + y + " " + unit;
    }
}
