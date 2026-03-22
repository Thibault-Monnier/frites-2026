package logic.action;

import math.TimeHelpers;

public class DelayAction implements Action {
    private final double delay;
    private final Runnable doWhile;

    private double start = -1;

    public DelayAction(double delaySec, Runnable doWhile) {
        this.delay = delaySec;
        this.doWhile = doWhile;
    }

    @Override
    public boolean run() {
        if (start < 0) start = TimeHelpers.getRuntime();

        boolean done = TimeHelpers.getRuntime() - start >= delay;
        if (!done) doWhile.run();
        return done;
    }
}
