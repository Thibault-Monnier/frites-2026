package org.firstinspires.ftc.teamcode.models;

public interface Clamp extends RobotModule {
    void open();

    void close();

    void toggle();

    String getStateKey();

    enum State {
        OPEN,
        CLOSED
    }
}
