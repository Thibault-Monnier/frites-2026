package utils.geometry;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

import java.util.Locale;

public class Position2D {
    private final double x;
    private final double y;
    private final DistanceUnit unit;

    public Position2D(DistanceUnit unit, double x, double y) {
        this.x = x;
        this.y = y;
        this.unit = unit;
    }

    public Position2D(Pose2D pose) {
        this.unit = DistanceUnit.MM;
        this.x = pose.getX(unit);
        this.y = pose.getY(unit);
    }

    public Position2D(Distance x, Distance y) {
        this(DistanceUnit.MM, x.toMillimeters(), y.toMillimeters());
    }

    public Position2D() {
        this(DistanceUnit.MM, 0, 0);
    }

    public static Position2D fromPosition(Position position) {
        return new Position2D(position.unit, position.x, position.y);
    }

    /// The x component converted to the desired distance unit
    public double getX(DistanceUnit unit) {
        return unit.fromUnit(this.unit, x);
    }

    /// The x component as a Distance object
    public Distance getX() {
        return new Distance(unit, x);
    }

    /// The y component converted to the desired distance unit
    public double getY(DistanceUnit unit) {
        return unit.fromUnit(this.unit, y);
    }

    /// The y component as a Distance object
    public Distance getY() {
        return new Distance(unit, y);
    }

    public boolean hasNaN() {
        return Double.isNaN(x) || Double.isNaN(y);
    }

    /// Translates this Position2D by a Vector2D
    public Position2D add(Vector2D translation) {
        Distance newX = getX().add(translation.getX());
        Distance newY = getY().add(translation.getY());
        return new Position2D(newX, newY);
    }

    /// Calculates the displacement vector
    public Vector2D subtract(Position2D other) {
        Distance newX = getX().subtract(other.getX());
        Distance newY = getY().subtract(other.getY());
        return new Vector2D(newX, newY);
    }

    /// Rotates the absolute position around the origin (0,0).
    /// Useful for transforming the coordinate system.
    public Position2D rotateAroundOrigin(Angle angle) {
        return new Position2D(
                getX().multiply(angle.cos()).subtract(getY().multiply(angle.sin())),
                getX().multiply(angle.sin()).add(getY().multiply(angle.cos())));
    }

    /// Calculates the Euclidean distance from this Position2D to another Position2D
    public Distance distanceTo(Position2D other) {
        return subtract(other).magnitude();
    }

    /// Calculates the angle from this Position2D to another Position2D
    public Angle angleTo(Position2D other) {
        return other.subtract(this).direction();
    }

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
}
