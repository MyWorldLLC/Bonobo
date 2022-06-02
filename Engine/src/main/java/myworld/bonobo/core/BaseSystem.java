package myworld.bonobo.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class BaseSystem {

    private final AtomicBoolean didInit;
    private final AtomicBoolean isEnabled;

    public BaseSystem(){
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
