package myworld.bonobo.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SystemManager {
    protected final List<BaseSystem> systems;

    public SystemManager(){
        systems = new CopyOnWriteArrayList<>();
    }

    public SystemManager register(BaseSystem system){
        return register(system, true);
    }

    public SystemManager register(BaseSystem system, boolean enable){
        if(system.needsInit()){
            system.systemInit();
        }
        systems.add(system);
        if(enable){
            system.enable();
        }
        return this;
    }

    public SystemManager registerAll(BaseSystem... systems){
        for(BaseSystem system : systems){
            register(system);
        }
        return this;
    }

    public void update(double tpf){
        for(BaseSystem system : systems){
            system.update(tpf);
        }
    }

    protected void stop(){
        for(BaseSystem system : systems){
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
