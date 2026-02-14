package logic.field;

import math.Distance;
import math.Position2D;

public class FieldElement {
    public final Position2D position;
    public final Distance width;
    public final Distance depth;
    public final Distance height;

    public FieldElement(Position2D position, Distance width, Distance depth, Distance height) {
        this.position = position;
        this.width = width;
        this.depth = depth;
        this.height = height;
    }

    public Distance halfWidth() {
        return width.divide(2);
    }

    public Distance halfDepth() {
        return depth.divide(2);
    }
}
