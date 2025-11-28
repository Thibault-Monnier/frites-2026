package org.firstinspires.ftc.teamcode.utils;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Position2D {
    protected final double x;
    protected final double y;
    protected final DistanceUnit distanceUnit;

    /**
     * Creates a new Position2D object.
     *
     * @param distanceUnit the unit of distance for both x and y
     * @param x the x position
     * @param y the y position
     */
    public Position2D(DistanceUnit distanceUnit, double x, double y) {
        this.x = x;
        this.y = y;
        this.distanceUnit = distanceUnit;
    }

    /**
     * Creates a new Position2D object from a Vector2d
     *
     * @param distanceUnit the unit of distance for both x and y
     * @param vec the vector containing x and y
     */
    public Position2D(DistanceUnit distanceUnit, Vector2d vec) {
        this(distanceUnit, vec.x, vec.y);
    }

    /**
     * Creates a new Position2D object from a Pose2D by extracting its x and y values
     *
     * @param pose the Pose2D object to create the Position2D from
     */
    public Position2D(Pose2D pose) {
        this.distanceUnit = DistanceUnit.MM;
        this.x = pose.getX(distanceUnit);
        this.y = pose.getY(distanceUnit);
    }

    /** Creates a new Position2D object at (0, 0) */
    public Position2D() {
        this(DistanceUnit.MM, 0, 0);
    }

    /**
     * This gets X in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the X member converted to the desired distance unit
     */
    public double getX(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, x);
    }

    /**
     * This gets the Y in the desired distance unit
     *
     * @param unit the desired distance unit
     * @return the Y member converted to the desired distance unit
     */
    public double getY(DistanceUnit unit) {
        return unit.fromUnit(this.distanceUnit, y);
    }

    /**
     * This returns a string representation of the object in a human readable format for debugging
     * purposes.
     *
     * @return a string representation of the object
     */
    @NonNull
    @Override
    public String toString() {
        return "(Pose2D) x=" + x + ", y=" + y + " " + distanceUnit;
    }
}
