package myworld.bonobo.time;

import java.util.function.Supplier;

public interface TimedLoop {

    void run(Supplier<Boolean> exitCondition, TimedRunnable body, long periodMillis);

}
