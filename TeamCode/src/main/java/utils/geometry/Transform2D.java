package utils.geometry;

import androidx.annotation.NonNull;

import java.util.Locale;

/// Represents the transform between two poses.
public class Transform2D {
    private final Vector2D translation;
    private final Angle rotation;

    public Transform2D(Vector2D translation, Angle rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }

    public Vector2D getTranslation() {
        return translation;
    }

    public Angle getRotation() {
        return rotation;
    }

    @NonNull
    public String toString() {
        return String.format(
                Locale.ENGLISH, "(Transform2D) translation=%s, rotation=%s", translation, rotation);
    }
}
