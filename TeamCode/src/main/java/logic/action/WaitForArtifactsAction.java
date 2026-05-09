package logic.action;

import modules.sensor.DistanceSensorMonitor;
import utils.TimeHelpers;

/**
 * Waits until both the specified delay has passed AND the robot has at least 3 artifacts as
 * reported by the DistanceSensorMonitor. While waiting, the provided Runnable is executed each
 * tick (same behaviour as DelayAction).
 */
public class WaitForArtifactsAction implements Action {
    private final double delay;
    private final Runnable doWhile;
    private final DistanceSensorMonitor distanceSensorMonitor;

    private double start = -1;

    public WaitForArtifactsAction(double delaySec, Runnable doWhile, DistanceSensorMonitor monitor) {
        this.delay = delaySec;
        this.doWhile = doWhile;
        this.distanceSensorMonitor = monitor;
    }

    @Override
    public boolean run() {
        if (start < 0) start = TimeHelpers.getRuntime();

        boolean timeDone = TimeHelpers.getRuntime() - start >= delay;
        boolean hasThree = distanceSensorMonitor != null
                && distanceSensorMonitor.getNumberOfArtifactsInRobot() >= 3;

        boolean done = timeDone && hasThree;
        if (!done && doWhile != null) doWhile.run();

        return done;
    }
}

