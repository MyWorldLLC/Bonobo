package myworld.bonobo.time;

import myworld.bonobo.util.ThreadUtil;

import java.util.function.Supplier;

public class SleepingTimedLoop implements TimedLoop {

    protected final Timer timer;

    public SleepingTimedLoop(Timer timer){
        this.timer = timer;
    }

    @Override
    public void run(Supplier<Boolean> exitCondition, TimedRunnable body, long periodMillis) {
        long lastStart = 0;
        long lastEnd = 0;
        long lastElapsed = 0;
        while(!exitCondition.get()){
            long startTime = timer.currentMillis();
            body.run(timer, lastStart, lastEnd, lastElapsed);
            long endTime = timer.currentMillis();

            long elapsed = endTime - startTime;
            long delta = periodMillis - elapsed;
            if(delta > 0){
                ThreadUtil.precisionSleep(timer, delta);
            }

            lastStart = startTime;
            lastEnd = endTime;
            lastElapsed = elapsed;
        }
    }
}
