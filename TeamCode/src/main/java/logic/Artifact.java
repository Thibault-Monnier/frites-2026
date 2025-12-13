package logic;

import math.Position2D;

public class Artifact extends FieldElement {
    public Color color;

    public Artifact(Position2D position, double width, double depth, double height) {
        super(position, width, depth, height);
    }

    public Artifact(Color color) {
        super(null, 0, 0, 0);
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public enum Color {
        PURPLE,
        GREEN
    }
}
