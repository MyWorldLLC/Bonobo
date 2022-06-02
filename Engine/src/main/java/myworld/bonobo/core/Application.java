package myworld.bonobo.core;

import myworld.bonobo.time.DefaultTimerSystem;
import myworld.bonobo.time.SleepingTimedLoop;
import myworld.bonobo.time.TimedLoop;
import myworld.bonobo.time.TimerSystem;
import myworld.bonobo.util.TimeUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class Application {

    private final AtomicBoolean stopRequested;
    protected final SystemManager systemManager;

    public Application(){
        stopRequested = new AtomicBoolean(false);
        systemManager = new SystemManager();
    }

    public void start(){
        initialize();
        run();
    }

    private void initialize(){
        initializeEngine();
        initializeApp();
    }

    private void initializeEngine(){
        systemManager.registerAll(
                new DefaultTimerSystem()
        );
    }

    public void initializeApp(){}

    private void run(){
        TimedLoop gameLoop = new SleepingTimedLoop(systemManager.getSystem(TimerSystem.class).getTimer());
        gameLoop.run(stopRequested::get, (timer, lastStart, lastEnd, lastDuration) -> {
            systemManager.update(TimeUtil.millisToSeconds(timer.currentMillis() - lastStart));
        }, TimeUtil.secondsToMillis(1/64.0)); // TODO - configurable update rate

        systemManager.stop();
    }

    public boolean isStopping(){
        return stopRequested.get();
    }

    public void stop(){
        stopRequested.set(true);
    }

    public SystemManager getSystemManager(){
        return systemManager;
    }
}
