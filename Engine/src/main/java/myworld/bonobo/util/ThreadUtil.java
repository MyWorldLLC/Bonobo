package myworld.bonobo.util;

import myworld.bonobo.time.Timer;

public class ThreadUtil {

    public static void precisionSleep(Timer timer, long millis){
        long elapsed = 0;
        long lastTime = timer.currentMillis();
        while(elapsed < millis){
            try{
                Thread.sleep(millis - elapsed);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
            elapsed = timer.currentMillis() - lastTime;
        }
    }

}
