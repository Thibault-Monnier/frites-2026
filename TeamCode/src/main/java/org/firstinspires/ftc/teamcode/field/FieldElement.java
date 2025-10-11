package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Vector2d;

public class FieldElement {
    public final Vector2d position;
    public final double width;
    public final double depth;
    public final double height;

    public FieldElement(Vector2d position, double width, double depth, double height) {
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
