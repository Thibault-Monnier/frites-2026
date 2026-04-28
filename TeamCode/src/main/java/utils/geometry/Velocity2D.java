package utils.geometry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Velocity2D {
    /// Per second
    private final Vector2D linear;
    /// Per second
    private final Angle angular;

    public Velocity2D(
            Vector2D linearDisplacement, @Nullable Angle angularDisplacement, double timeSec) {
        double scalar = timeSec > 0 ? 1 / timeSec : 0;
        this.linear = linearDisplacement.scale(scalar);
        this.angular =
                angularDisplacement != null ? angularDisplacement.multiply(scalar) : new Angle();
    }

    public Velocity2D(Pose2D first, Pose2D second, double intervalSec) {
        this(
                second.subtract(first).getTranslation(),
                second.subtract(first).getRotation(),
                intervalSec);
    }

    public Velocity2D(Position2D first, Position2D second, double intervalSec) {
        this(second.subtract(first), null, intervalSec);
    }

    /// The transform per second
    public Transform2D getRawVelocity() {
        return new Transform2D(linear, angular);
    }

    /// Per second
    public Angle getAngularVelocity() {
        return angular;
    }

    /// Per second
    public Vector2D getLinearVelocity() {
        return linear;
    }

    @NonNull
    public String toString() {
        return getRawVelocity().toString() + " / sec";
    }
}
