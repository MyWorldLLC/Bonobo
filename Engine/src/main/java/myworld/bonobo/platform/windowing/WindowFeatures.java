package myworld.bonobo.platform.windowing;

public record WindowFeatures(String title, int width, int height, boolean startVisible) {

    public WindowFeatures(String title, int width, int height){
        this(title, width, height, true);
    }
}
