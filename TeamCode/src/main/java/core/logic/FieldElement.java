package core.logic;


import core.math.Position2D;

public class FieldElement {
    public final Position2D position;
    public final double width;
    public final double depth;
    public final double height;

    public FieldElement(Position2D position, double width, double depth, double height) {
        this.position = position;
        this.width = width;
        this.depth = depth;
        this.height = height;
    }

    public double halfWidth() {
        return width / 2;
    }

    public double halfDepth() {
        return depth / 2;
    }
}
