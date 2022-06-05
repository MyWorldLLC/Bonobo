/*
 * Copyright 2022 MyWorld, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
