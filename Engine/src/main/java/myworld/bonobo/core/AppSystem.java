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

package myworld.bonobo.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class AppSystem {

    private final AtomicBoolean didInit;
    private final AtomicBoolean isEnabled;

    public AppSystem(){
        didInit = new AtomicBoolean(false);
        isEnabled = new AtomicBoolean(false);
    }

    public boolean needsInit(){
        return !didInit.get();
    }

    protected final void systemInit(){
        didInit.set(true);
        initialize();
    }

    public void initialize(){}

    public final void enable(){
        if(!isEnabled.getAndSet(true)){
            onEnabled();
        }
    }

    protected void onEnabled(){}

    public void update(double tpf){}

    public final void disable(){
        if(isEnabled.getAndSet(false)){
            onDisabled();
        }
    }

    protected void onDisabled(){}

    public void stop(){}
}
