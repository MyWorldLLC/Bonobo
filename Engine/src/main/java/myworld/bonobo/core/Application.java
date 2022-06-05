/*
 * Copyright 2022 MyWorld, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package myworld.bonobo.core;

import myworld.bonobo.platform.GlfwWindowSystem;
import myworld.bonobo.render.VulkanRenderSystem;
import myworld.bonobo.time.DefaultClockSystem;
import myworld.bonobo.time.SleepingTimedLoop;
import myworld.bonobo.time.TimedLoop;
import myworld.bonobo.time.ClockSystem;
import myworld.bonobo.util.TimeUtil;
import myworld.bonobo.util.log.Logger;

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
        Logger.init();
        systemManager.registerAll(
                new DefaultClockSystem(),
                new GlfwWindowSystem(this),
                new VulkanRenderSystem(this)
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

    public <T> T getSystem(Class<T> systemCls){
        return systemManager.getSystem(systemCls);
    }
}
