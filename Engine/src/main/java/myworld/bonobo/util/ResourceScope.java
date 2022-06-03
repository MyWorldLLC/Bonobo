package myworld.bonobo.util;

import java.util.ArrayList;
import java.util.List;

public class ResourceScope implements AutoCloseable {

    protected final List<AutoCloseable> closeables;

    public ResourceScope(){
        closeables = new ArrayList<>();
    }

    public <T extends AutoCloseable> T add(T freeable){
        closeables.add(freeable);
        return freeable;
    }

    @Override
    public void close() throws Exception {
        for(AutoCloseable resource : closeables){
            resource.close();
        }
        closeables.clear();
    }
}
