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
