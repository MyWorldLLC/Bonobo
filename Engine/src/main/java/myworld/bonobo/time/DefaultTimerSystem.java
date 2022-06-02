package myworld.bonobo.time;

import myworld.bonobo.core.AppSystem;

public class DefaultTimerSystem extends AppSystem implements TimerSystem {

    protected final Clock clock = new SystemClock();

    public Clock getTimer(){
        return clock;
    }

}
