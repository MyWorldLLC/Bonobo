package myworld.bonobo.time;

import myworld.bonobo.core.AppSystem;

public class DefaultClockSystem extends AppSystem implements ClockSystem {

    protected final Clock clock = new SystemClock();

    public Clock getClock(){
        return clock;
    }

}
