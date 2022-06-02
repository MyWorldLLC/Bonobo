package myworld.bonobo.time;

public interface DeltaTimedTask {
    void run(Clock clock, long lastStart, long lastEnd, long lastElapsed);
}
