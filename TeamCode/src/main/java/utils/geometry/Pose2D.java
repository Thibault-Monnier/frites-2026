package utils.geometry;

import androidx.annotation.NonNull;

import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;

import config.FieldConfig;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

public class Pose2D {
    private final Position2D position;
    private final Angle heading;

    public Pose2D(
            DistanceUnit distanceUnit, double x, double y, AngleUnit headingUnit, double heading) {
        this.position = new Position2D(distanceUnit, x, y);
        this.heading = new Angle(headingUnit, heading);
    }

    public Pose2D(Distance x, Distance y, Angle heading) {
        this(
                DistanceUnit.MM,
                x.getValue(DistanceUnit.MM),
                y.getValue(DistanceUnit.MM),
                AngleUnit.RADIANS,
                heading.getValue(AngleUnit.RADIANS));
    }

    public Pose2D(Position2D position, Angle heading) {
        this(position.getX(), position.getY(), heading);
    }

    public Pose2D() {
        this(DistanceUnit.MM, 0, 0, AngleUnit.RADIANS, 0);
    }

    public Position2D getPosition() {
        return position;
    }

    /// The x component converted to the desired distance unit
    public double getX(DistanceUnit unit) {
        return position.getX(unit);
    }

    /// The x component as a Distance object
    public Distance getX() {
        return position.getX();
    }

    /// The y component converted to the desired distance unit
    public double getY(DistanceUnit unit) {
        return position.getY(unit);
    }

    /// The y component as a Distance object
    public Distance getY() {
        return position.getY();
    }

    /// The heading converted to the desired angle unit
    public double getHeading(AngleUnit unit) {
        return heading.getValue(unit);
    }

    /// The heading as an Angle object
    public Angle getHeading() {
        return heading;
    }

    public boolean hasNaN() {
        return position.hasNaN() || heading.isNaN();
    }

    /// Translates this Pose2D by a Vector2D
    public Pose2D add(Vector2D other) {
        return new Pose2D(position.add(other), heading);
    }

    /// Rotates the heading of this Pose2D by the given angle
    public Pose2D add(Angle other) {
        return new Pose2D(position, heading.add(other));
    }

    public Pose2D add(Transform2D other) {
        return new Pose2D(position.add(other.getTranslation()), heading.add(other.getRotation()));
    }

    public Transform2D subtract(Pose2D other) {
        return new Transform2D(
                position.subtract(other.getPosition()), heading.subtract(other.getHeading()));
    }

    /// Converts a local displacement into world coordinates and adds it to this pose's position
    public Position2D addRelative(Vector2D other) {
        Vector2D rotatedOther = other.rotate(heading);
        return position.add(rotatedOther);
    }

    /// Converts this Pose2D to an FTC navigation Pose2D object.
    public org.firstinspires.ftc.robotcore.external.navigation.Pose2D toNavigationPose2D() {
        return new org.firstinspires.ftc.robotcore.external.navigation.Pose2D(
                DistanceUnit.MM,
                getX(DistanceUnit.MM),
                getY(DistanceUnit.MM),
                AngleUnit.RADIANS,
                getHeading(AngleUnit.RADIANS));
    }

    /// Converts this Pose2D to a PedroCoordinates Pose, changing the coordinate system accordingly.
    public Pose toPedropathingPose() {
        Pose pose =
                new Pose(
                        getX(DistanceUnit.INCH),
                        getY(DistanceUnit.INCH),
                        getHeading(AngleUnit.RADIANS),
                        FTCCoordinates.INSTANCE);
        return pose.getAsCoordinateSystem(PedroCoordinates.INSTANCE);
    }

    /// Converts an FTC navigation Pose2D object to a Pose2D object.
    public static Pose2D fromNavigationPose2D(
            org.firstinspires.ftc.robotcore.external.navigation.Pose2D navPose) {
        return new Pose2D(
                DistanceUnit.MM,
                navPose.getX(DistanceUnit.MM),
                navPose.getY(DistanceUnit.MM),
                AngleUnit.RADIANS,
                navPose.getHeading(AngleUnit.RADIANS));
    }

    /// Converts a PedroCoordinates Pose to a Pose2D, changing the coordinate system accordingly.
    public static Pose2D fromPedropathingPose(Pose pose) {
        double x = pose.getX();
        double y = pose.getY();
        double heading = pose.getHeading();

        x -= FieldConfig.FIELD_SIZE.toInches() / 2;
        y -= FieldConfig.FIELD_SIZE.toInches() / 2;

        double newX = -y;
        double newY = x;
        double newHeading = heading + Math.PI / 2;

        return new Pose2D(DistanceUnit.INCH, newX, newY, AngleUnit.RADIANS, newHeading);
    }

    @NonNull
    public String toString() {
        return String.format(
                Locale.ENGLISH, "(Pose2D) x=%s, y=%s, heading=%s", getX(), getY(), getHeading());
    }
}
