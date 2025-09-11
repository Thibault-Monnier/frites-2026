package org.firstinspires.ftc.teamcode.models;

import java.util.HashMap;

public interface RobotModule {
    void apply();

    HashMap<String, Object> getCurrentState();

    void setState(HashMap<String, String> state);
}
