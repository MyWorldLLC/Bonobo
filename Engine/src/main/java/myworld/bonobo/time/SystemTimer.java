package myworld.bonobo.time;

public class SystemTimer implements Timer {

    protected final double NANOS_TO_MILLIS = 1e-6;

    @Override
    public long currentMillis() {
        return Math.round(System.nanoTime() * NANOS_TO_MILLIS);
    }
}
