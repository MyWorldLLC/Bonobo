package myworld.bonobo.platform.windowing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WindowManager<T extends Window> {

    public static final int FIRST_WINDOW_ID = 1;

    protected final List<T> windows;
    protected final WindowFactory<T> factory;
    protected final AtomicInteger idCounter;

    public WindowManager(WindowFactory<T> factory){
        windows = new ArrayList<>();
        this.factory = factory;
        idCounter = new AtomicInteger(FIRST_WINDOW_ID);
    }

    public T createWindow(WindowFeatures features){
        var window = factory.create(nextWindowId(), features);
        windows.add(window);
        return window;
    }

    public List<T> getWindows(){
        return windows;
    }

    public Window getWindow(int windowId){
        return windows.stream()
                .filter(w -> w.getId() == windowId)
                .findFirst()
                .orElse(null);
    }

    public void closeWindow(T window){
        windows.removeIf(w -> w.id == window.id);
    }

    protected int nextWindowId(){
        return idCounter.getAndIncrement();
    }
}
