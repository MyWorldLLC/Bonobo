package myworld.bonobo.platform;

public class Window {

    protected final long handle;

    protected Window(long handle){
        this.handle = handle;
    }

    protected long getHandle(){
        return handle;
    }

}
