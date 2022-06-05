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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SystemManager {
    protected final List<AppSystem> systems;

    public SystemManager(){
        systems = new CopyOnWriteArrayList<>();
    }

    public SystemManager register(AppSystem system){
        return register(system, true);
    }

    public SystemManager register(AppSystem system, boolean enable){
        if(system.needsInit()){
            system.systemInit();
        }
        systems.add(system);
        if(enable){
            system.enable();
        }
        return this;
    }

    public SystemManager registerAll(AppSystem... systems){
        for(AppSystem system : systems){
            register(system);
        }
        return this;
    }

    public void update(double tpf){
        for(AppSystem system : systems){
            system.update(tpf);
        }
    }

    protected void stop(){
        for(AppSystem system : systems){
            system.stop();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getSystem(Class<T> systemCls){
        return (T) systems.stream()
                .filter(s -> clsMatch(s.getClass(), systemCls))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAllSystems(Class<T> systemCls){
        return (List<T>) systems.stream()
                .filter(s -> clsMatch(s.getClass(), systemCls))
                .collect(Collectors.toList());
    }

    public <T> void remove(Class<T> systemCls){
        systems.removeIf(s -> s.getClass().equals(systemCls));
    }

    private boolean clsMatch(Class<?> test, Class<?> target){
        return target.isAssignableFrom(test);
    }
}
