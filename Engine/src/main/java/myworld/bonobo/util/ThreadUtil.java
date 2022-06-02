package myworld.bonobo.util;

import myworld.bonobo.time.Timer;

import java.util.concurrent.locks.LockSupport;

public class ThreadUtil {

    public static void precisionSleep(Timer timer, long millis){
        long elapsed = 0;
        long lastTime = timer.currentMillis();
        while(elapsed < millis){
            LockSupport.parkNanos(TimeUtil.millisToNanos(millis - elapsed));
            elapsed = timer.currentMillis() - lastTime;
        }
    }

}
