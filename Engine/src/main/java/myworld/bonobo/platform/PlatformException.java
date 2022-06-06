package myworld.bonobo.platform;

public class PlatformException extends RuntimeException {

    public PlatformException(String msg){
        super(msg);
    }

    public PlatformException(Throwable t){
        super(t);
    }

    public PlatformException(String msg, Throwable t){
        super(msg, t);
    }
}
