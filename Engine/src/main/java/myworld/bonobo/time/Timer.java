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

public class Timer {

    protected double elapsed;
    protected double period;
    protected final PeriodicTimedTask task;

    public Timer(double period, PeriodicTimedTask task){
        elapsed = 0;
        this.period = period;
        this.task = task;
    }

    public static Timer create(double period, PeriodicTimedTask task){
        return new Timer(period, task);
    }

    public void setPeriod(double period){
        this.period = period;
    }

    public double getPeriod(){
        return period;
    }

    public double getElapsed(){
        return elapsed;
    }

    public void tick(double timeStep){
        elapsed += timeStep;
        if(elapsed >= period){
            task.run(elapsed, period, timeStep);
            elapsed = 0;
        }
    }

}
