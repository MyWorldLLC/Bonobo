package myworld.bonobo.core;

public class ApplicationStoppingException extends RuntimeException {

    public ApplicationStoppingException(){
        super();
    }

    public ApplicationStoppingException(String msg){
        super(msg);
    }

    public ApplicationStoppingException(Throwable t){
        super(t);
    }

    public ApplicationStoppingException(String msg, Throwable t){
        super(msg, t);
    }
}
