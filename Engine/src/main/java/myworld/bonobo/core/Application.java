package myworld.bonobo.core;

import myworld.bonobo.platform.GlfwWindowSystem;
import myworld.bonobo.render.VulkanRenderSystem;
import myworld.bonobo.time.DefaultClockSystem;
import myworld.bonobo.time.SleepingTimedLoop;
import myworld.bonobo.time.TimedLoop;
import myworld.bonobo.time.ClockSystem;
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
                new DefaultClockSystem(),
                new GlfwWindowSystem(this),
                new VulkanRenderSystem()
        );
    }

    public void initializeApp(){}

    private void run(){
        TimedLoop gameLoop = new SleepingTimedLoop(systemManager.getSystem(ClockSystem.class).getClock());
        gameLoop.run(stopRequested::get, (clock, lastStart, lastEnd, lastDuration) -> {
            systemManager.update(TimeUtil.millisToSeconds(clock.currentMillis() - lastStart));
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
