package logic;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import java.util.function.Supplier;

public class DeferredAction implements Action {
    private final Supplier<Action> actionFactory;
    private Action builtAction = null;

    public DeferredAction(Supplier<Action> actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public boolean run(@NonNull TelemetryPacket packet) {
        // If the action hasn't been built yet, build it now using the CURRENT robot state
        if (builtAction == null) {
            builtAction = actionFactory.get();
        }
        // Run the newly built action
        return builtAction.run(packet);
    }
}
