package models;

public interface Clamp extends RobotModule {
    void open();

    void close();

    String getStateKey();

    enum State {
        OPEN,
        CLOSED
    }
}
