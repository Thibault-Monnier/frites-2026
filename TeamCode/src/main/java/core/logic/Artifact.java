package core.logic;

import core.math.Position2D;

public class Artifact extends FieldElement {
    public ArtifactColor color;

    public Artifact(Position2D position, double width, double depth, double height) {
        super(position, width, depth, height);
    }

    public Artifact(ArtifactColor color) {
        super(null, 0, 0, 0);
        this.color = color;
    }

    public void setColor(ArtifactColor color) {
        this.color = color;
    }
}
