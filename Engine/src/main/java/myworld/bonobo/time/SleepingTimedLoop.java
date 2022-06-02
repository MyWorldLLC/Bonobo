package myworld.bonobo.time;

import myworld.bonobo.util.ThreadUtil;

import java.util.function.Supplier;

public class SleepingTimedLoop implements TimedLoop {

    protected final Clock clock;

    public SleepingTimedLoop(Clock clock){
        this.clock = clock;
    }

    @Override
    public void run(Supplier<Boolean> exitCondition, DeltaTimedTask body, long periodMillis) {
        long lastStart = 0;
        long lastEnd = 0;
        long lastElapsed = 0;
        while(!exitCondition.get()){
            long startTime = clock.currentMillis();
            body.run(clock, lastStart, lastEnd, lastElapsed);
            long endTime = clock.currentMillis();

            long elapsed = endTime - startTime;
            long delta = periodMillis - elapsed;
            if(delta > 0){
                ThreadUtil.precisionSleep(clock, delta);
            }

            lastStart = startTime;
            lastEnd = endTime;
            lastElapsed = elapsed;
        }
    }
}
