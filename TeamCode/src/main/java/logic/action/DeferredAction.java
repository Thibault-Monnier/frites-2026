package logic.action;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import java.util.function.Supplier;

public class DeferredAction implements Action {
    private final Supplier<Action> actionFactory;
    private Action builtAction = null;

    public DeferredAction(Supplier<Action> actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public boolean run(@NonNull TelemetryPacket packet) {
        if (builtAction == null) {
            builtAction = actionFactory.get();
        }
        return builtAction.run(packet);
    }
}
