package myworld.bonobo.time;

import java.util.function.Supplier;

public interface TimedLoop {

    void run(Supplier<Boolean> exitCondition, DeltaTimedTask body, long periodMillis);

}
