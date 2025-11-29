package core.modules;

import java.util.HashMap;

public interface RobotModule {
    void apply();

    HashMap<String, Object> getCurrentState();

    void setState(HashMap<String, String> state);
}
