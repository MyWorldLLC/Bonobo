package myworld.bonobo.util.log;

import static java.lang.System.Logger.Level;

public class Logger {

    protected final System.Logger log;

    private Logger(System.Logger log){
        this.log = log;
    }

    public void trace(String msg, Object... fmtArgs){
        log(Level.TRACE, msg, fmtArgs);
    }

    public void debug(String msg, Object... fmtArgs){
        log(Level.DEBUG, msg, fmtArgs);
    }

    public void info(String msg, Object... fmtArgs){
        log(Level.INFO, msg, fmtArgs);
    }

    public void warning(String msg, Object... fmtArgs){
        log(Level.WARNING, msg, fmtArgs);
    }

    public void error(String msg, Object... fmtArgs){
        log(Level.ERROR, msg, fmtArgs);
    }

    public void log(Level level, String msg, Object... fmtArgs){
        log.log(level, msg.formatted(fmtArgs));
    }

    public System.Logger logger(){
        return log;
    }

    public static Logger loggerFor(Class<?> cls){
        return new Logger(System.getLogger(cls.getName()));
    }
}
