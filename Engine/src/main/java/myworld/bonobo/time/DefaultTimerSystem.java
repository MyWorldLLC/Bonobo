package myworld.bonobo.time;

import myworld.bonobo.core.BaseSystem;

public class DefaultTimerSystem extends BaseSystem implements TimerSystem {

    protected final Timer timer = new SystemTimer();

    public Timer getTimer(){
        return timer;
    }

}
