package core.modules.actuator;

import java.util.HashMap;

public interface RobotActuatorModule {
    void apply();

    HashMap<String, Object> getCurrentState();

    void setState(HashMap<String, String> state);
}
