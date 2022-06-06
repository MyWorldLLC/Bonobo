package myworld.bonobo.platform.windowing;

public interface WindowFactory<T extends Window> {
    T create(int id, WindowFeatures features);
}
