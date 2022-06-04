package myworld.bonobo.render;

import static java.lang.System.Logger.Level;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.*;

import myworld.bonobo.core.AppSystem;
import myworld.bonobo.core.Application;
import myworld.bonobo.util.ResourceScope;
import myworld.bonobo.util.LogUtil;

import org.lwjgl.system.MemoryStack;

public class VulkanRenderSystem extends AppSystem {

    public static final String ENGINE_NAME = "Bonobo";
    public static final String RENDERER_NAME = "Bonobo VK";

    private static final System.Logger log = LogUtil.loggerFor(VulkanRenderSystem.class);
    protected final Application app;
    protected final ResourceScope systemScope;

    protected VulkanInstance instance;

    public VulkanRenderSystem(Application app){
        this.app = app;
        systemScope = new ResourceScope();
    }

    @Override
    public void initialize(){

        // If used with the default windowing implementation, this will already have happened.
        // GLFW allows multiple init calls, and subsequent calls (following a successful init)
        // have no effect.
        if(!glfwInit()){
            log.log(Level.INFO, "Could not initialize GLFW, exiting");
            app.stop();
        }

        if(!glfwVulkanSupported()){
            log.log(Level.INFO, "Vulkan is not supported on this device, exiting");
            app.stop();
        }

        log.log(Level.INFO, "Initializing Vulkan renderer");

        instance = systemScope.add(VulkanInstance.create(ENGINE_NAME, RENDERER_NAME));
        try(var stack = MemoryStack.stackPush()){


           // TODO
        }
    }

    public VulkanInstance getInstance(){
        return instance;
    }

    @Override
    public void stop() {
        try{
            systemScope.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
