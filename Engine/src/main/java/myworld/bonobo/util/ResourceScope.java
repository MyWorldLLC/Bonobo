package myworld.bonobo.util;

import org.lwjgl.system.NativeResource;

import java.util.ArrayList;
import java.util.List;

public class ResourceScope implements AutoCloseable {

    protected final List<NativeResource> freeables;

    public ResourceScope(){
        freeables = new ArrayList<>();
    }

    public <T extends NativeResource> T add(T freeable){
        freeables.add(freeable);
        return freeable;
    }

    public void free(){
        for(NativeResource resource : freeables){
            resource.free();
        }
        freeables.clear();
    }

    @Override
    public void close() {
        free();
    }
}
