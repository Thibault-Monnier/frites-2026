package logic.action;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

public interface Action {
    /** Runs the action. Returns true if the action is completed, false otherwise. */
    boolean run(TelemetryPacket packet);
}
