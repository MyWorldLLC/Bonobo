package myworld.bonobo.util;

import myworld.bonobo.time.Clock;

public class ThreadUtil {

    public static void precisionSleep(Clock clock, long millis){
        long elapsed = 0;
        long lastTime = clock.currentMillis();
        while(elapsed < millis){
            try{
                Thread.sleep(millis - elapsed);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
            elapsed = clock.currentMillis() - lastTime;
        }
    }

}
