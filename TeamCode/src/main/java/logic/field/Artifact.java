package logic.field;

import androidx.annotation.NonNull;

public class Artifact {
    public Color color;

    public Artifact(Color color) {
        this.color = color;
    }

    public enum Color {
        PURPLE {
            @NonNull
            public String toString() {
                return "P";
            }
        },
        GREEN {
            @NonNull
            public String toString() {
                return "G";
            }
        }
    }

    public enum Row {
        FRONT,
        MIDDLE,
        BACK
    }
}
