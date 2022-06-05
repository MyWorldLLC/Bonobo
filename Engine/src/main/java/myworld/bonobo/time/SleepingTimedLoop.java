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

package myworld.bonobo.time;

import myworld.bonobo.util.ThreadUtil;

import java.util.function.Supplier;

public class SleepingTimedLoop implements TimedLoop {

    protected final Clock clock;

    public SleepingTimedLoop(Clock clock){
        this.clock = clock;
    }

    @Override
    public void run(Supplier<Boolean> exitCondition, DeltaTimedTask body, long periodMillis) {
        long lastStart = 0;
        long lastEnd = 0;
        long lastElapsed = 0;
        while(!exitCondition.get()){
            long startTime = clock.currentMillis();
            body.run(clock, lastStart, lastEnd, lastElapsed);
            long endTime = clock.currentMillis();

            long elapsed = endTime - startTime;
            long delta = periodMillis - elapsed;
            if(delta > 0){
                ThreadUtil.precisionSleep(clock, delta);
            }

            lastStart = startTime;
            lastEnd = endTime;
            lastElapsed = elapsed;
        }
    }
}
