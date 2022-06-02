package myworld.bonobo.util;

public class TimeUtil {

    public static final double MILLIS_PER_SECOND = 1e3;
    public static final double NANOS_PER_SECOND = 1e9;
    public static final long NANOS_PER_MILLI = 1_000_000;

    public static long secondsToMillis(double seconds){
        return Math.round(seconds * MILLIS_PER_SECOND);
    }

    public static double millisToSeconds(long millis){
        return millis / MILLIS_PER_SECOND;
    }

    public static long millisToNanos(long millis){
        return millis * NANOS_PER_MILLI;
    }
}
