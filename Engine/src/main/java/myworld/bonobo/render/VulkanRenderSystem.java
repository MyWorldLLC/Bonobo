package myworld.bonobo.render;

import static java.lang.System.Logger.Level;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;

import myworld.bonobo.core.AppSystem;
import myworld.bonobo.core.Application;
import myworld.bonobo.util.ResourceScope;
import myworld.bonobo.util.log.Logger;

import org.lwjgl.system.MemoryStack;

public class VulkanRenderSystem extends AppSystem {

    public static final String ENGINE_NAME = "Bonobo";
    public static final String RENDERER_NAME = "Bonobo VK";

    private static final Logger log = Logger.loggerFor(VulkanRenderSystem.class);
    protected final Application app;
    protected final ResourceScope systemScope;

    protected Instance instance;

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
            log.error("Could not initialize GLFW, exiting");
            app.stop();
        }

        if(!glfwVulkanSupported()){
            log.error("Vulkan is not supported on this device, exiting");
            app.stop();
        }

        log.info("Initializing Vulkan renderer");

        instance = systemScope.add(Instance.create(ENGINE_NAME, RENDERER_NAME));
        try(var stack = MemoryStack.stackPush()){
            instance.getGpus().forEach(gpu -> {
                gpu.getProperties();
                log.info(gpu.getProperties().deviceNameString());
                log.info("Supports swapchain? %s", gpu.hasExtension(VK_KHR_SWAPCHAIN_EXTENSION_NAME));
            });


           // TODO
        }
    }

    public Instance getInstance(){
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
