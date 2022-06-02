package myworld.bonobo.platform;

import myworld.bonobo.core.AppSystem;
import myworld.bonobo.core.Application;
import myworld.bonobo.util.LogUtil;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import static java.lang.System.Logger.Level;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GlfwWindowSystem extends AppSystem {

    private static final System.Logger log = LogUtil.loggerFor(GlfwWindowSystem.class);

    protected final Application application;

    protected GLFWErrorCallback errorCallback;

    protected Window window;

    public GlfwWindowSystem(Application application){
        this.application = application;
    }

    @Override
    public void initialize(){
        log.log(Level.INFO, "Running on LWJGL " + Version.getVersion());

        errorCallback = GLFWErrorCallback.createPrint(System.err).set();

        glfwSetErrorCallback(errorCallback);
        if(!glfwInit()){
            log.log(Level.ERROR, "Could not initialize GLFW, exiting");
            application.stop();
        }
        log.log(Level.INFO, "Initialized GLFW successfully");

        // Don't create an OpenGL context by default
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        window = new Window(glfwCreateWindow(640, 480, "Bonobo", NULL, NULL));
        if (window.getHandle() == NULL) {
            log.log(Level.ERROR, "Failed to create a window, exiting");
            application.stop();
        }
    }

    @Override
    public void update(double tpf){
        glfwPollEvents();
        if(glfwWindowShouldClose(window.getHandle())){
            log.log(Level.INFO, "Window close requested, exiting");
            application.stop();
        }
    }

    @Override
    public void stop(){
        glfwDestroyWindow(window.getHandle());
        glfwTerminate();
    }
}
