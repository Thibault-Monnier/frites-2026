package logic.action;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

@FunctionalInterface
public interface ActionFunction {
    boolean run(TelemetryPacket packet);
}
