package myworld.bonobo.math;

public record Color(float r, float g, float b, float a) {

    public static final Color BLACK = new Color(0,0,0,1);
    public static final Color WHITE = new Color(1,1,1,1);
    public static final Color DARK_GREY = new Color(0.05f,0.05f,0.05f,1);
    public static final Color LIGHT_GREY = new Color(0.5f,0.5f,0.5f,1);

}
