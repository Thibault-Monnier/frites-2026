package utils;

import java.util.concurrent.TimeUnit;

public class TimeHelpers {
    /**
     * Returns the current runtime in seconds. NOTE: This is NOT the same as the OpMode runtime, as
     * it does not reset on init.
     */
    public static double getRuntime() {
        final double NANOSECONDS_PER_SECOND = TimeUnit.SECONDS.toNanos(1);
        return System.nanoTime() / NANOSECONDS_PER_SECOND;
    }
}
