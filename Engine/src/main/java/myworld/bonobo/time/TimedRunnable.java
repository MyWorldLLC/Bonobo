package myworld.bonobo.time;

public interface TimedRunnable {
    void run(Timer timer, long lastStart, long lastEnd, long lastElapsed);
}
