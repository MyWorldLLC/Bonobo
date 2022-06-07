package myworld.bonobo.platform.windowing;

public record WindowFeatures(String title, int width, int height, DisplayState state) {

    public record DisplayState(boolean startVisible, boolean borderless){}

    public WindowFeatures(String title, int width, int height){
        this(title, width, height, new DisplayState(true, false));
    }
}
