package myworld.bonobo.render;

import static java.lang.System.Logger.Level;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;

import myworld.bonobo.core.AppSystem;
import myworld.bonobo.core.Application;
import myworld.bonobo.util.ResourceScope;
import myworld.bonobo.util.LogUtil;
import static myworld.bonobo.render.VkErrUtil.check;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.ArrayList;
import java.util.List;

public class VulkanRenderSystem extends AppSystem {

    public static final String ENGINE_NAME = "Bonobo";
    public static final String RENDERER_NAME = "Bonobo VK";

    private static final System.Logger log = LogUtil.loggerFor(VulkanRenderSystem.class);
    protected final Application app;
    protected final ResourceScope systemScope;

    protected VkInstance vulkan;
    protected List<VkPhysicalDevice> gpus;

    public VulkanRenderSystem(Application app){
        this.app = app;
        systemScope = new ResourceScope();
        gpus = new ArrayList<>();
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
        try(var stack = MemoryStack.stackPush()){

            var requiredExtensions = getRequiredVulkanExtensions();
            if(requiredExtensions == null){
                throw new VulkanInitException("Could not query required Vulkan extensions, exiting");
            }

            var appName = stack.UTF8(ENGINE_NAME);
            var rendererName = stack.UTF8(RENDERER_NAME);

            var appInfo = VkApplicationInfo.calloc(stack)
                    .sType$Default()
                    .pNext(NULL)
                    .pApplicationName(appName)
                    .applicationVersion(0)
                    .pEngineName(rendererName)
                    .engineVersion(0)
                    .apiVersion(VK.getInstanceVersionSupported());

            var instanceInfo = VkInstanceCreateInfo.calloc(stack)
                    .sType$Default()
                    .pNext(NULL)
                    .flags(0)
                    .pApplicationInfo(appInfo)
                    .ppEnabledLayerNames(null)
                    .ppEnabledExtensionNames(null);

            var instancePtr = MemoryUtil.memCallocPointer(1);
            var result = vkCreateInstance(instanceInfo, null, instancePtr);
            if(result == VK_ERROR_INCOMPATIBLE_DRIVER){
                throw new VulkanInitException("Cannot find a compatible Vulkan installable client driver");
            }else if(result == VK_ERROR_EXTENSION_NOT_PRESENT){
                throw new VulkanInitException("This Vulkan driver does not have all required extensions");
            }else if(result != VK_SUCCESS){
                throw VulkanInitException.forError(result, "Could not create Vulkan instance");
            }

            vulkan = new VkInstance(instancePtr.get(0), instanceInfo);
            // TODO
        }
    }

    private static String[] getRequiredVulkanExtensions(){
        var stringPtrs = glfwGetRequiredInstanceExtensions();
        if(stringPtrs == null){
            return null;
        }
        var extensions = new String[stringPtrs.remaining()];
        for(int i = 0; i < extensions.length; i++){
            extensions[i] = stringPtrs.getStringASCII(i);
        }
        return extensions;
    }

    @Override
    public void stop(){
        if(vulkan != null){
            vkDestroyInstance(vulkan, null);
        }
        systemScope.free();
    }
}
