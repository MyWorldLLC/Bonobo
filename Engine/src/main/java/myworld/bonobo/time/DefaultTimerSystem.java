package myworld.bonobo.time;

import myworld.bonobo.core.AppSystem;

public class DefaultTimerSystem extends AppSystem implements TimerSystem {

    protected final Timer timer = new SystemTimer();

    public Timer getTimer(){
        return timer;
    }

}
