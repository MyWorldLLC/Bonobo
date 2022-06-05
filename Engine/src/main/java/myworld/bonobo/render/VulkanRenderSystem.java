package myworld.bonobo.render;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;

import myworld.bonobo.core.AppSystem;
import myworld.bonobo.core.Application;
import myworld.bonobo.util.ResourceScope;
import myworld.bonobo.util.log.Logger;

import org.lwjgl.system.MemoryStack;

import java.util.Comparator;

public class VulkanRenderSystem extends AppSystem {

    public static final String ENGINE_NAME = "Bonobo";
    public static final String RENDERER_NAME = "Bonobo VK";

    private static final Logger log = Logger.loggerFor(VulkanRenderSystem.class);
    protected final Application app;
    protected final ResourceScope systemScope;

    protected Instance instance;
    protected RenderingDevice device;

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
            // Use only an integrated or discrete gpu, and prefer discrete gpus to integrated
            // TODO - we need a much more intelligent way to choose the device, and it needs
            // to be able to be overridden via configuration
            var gpus = instance.getGpus().stream()
                    .filter(d -> {
                        int type = d.getProperties().deviceType();
                        return type == VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU
                                || type == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU;
                    }).sorted(
                            Comparator.comparingInt((PhysicalDevice d) -> d.getProperties().deviceType())
                                    .reversed())
                    .toList();

            gpus.forEach(gpu -> {
                gpu.getProperties();
                log.info("Found GPU: %s", gpu.getProperties().deviceNameString());
            });

            if(gpus.isEmpty()){
                log.error("No suitable GPU was found on this system, exiting");
                app.stop();
            }

            var gpu = gpus.get(0);
            device = RenderingDevice.create(gpu, 0); // TODO - select queue family


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
