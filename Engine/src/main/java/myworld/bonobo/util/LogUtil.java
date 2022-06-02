package myworld.bonobo.util;

public class LogUtil {

    public static System.Logger loggerFor(Class<?> cls){
        return System.getLogger(cls.getName());
    }
}
