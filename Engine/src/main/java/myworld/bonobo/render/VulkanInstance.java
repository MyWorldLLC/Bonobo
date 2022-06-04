package myworld.bonobo.render;

import myworld.bonobo.util.LogUtil;
import static java.lang.System.Logger.Level;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.*;

import static myworld.bonobo.render.VkErrUtil.check;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VulkanInstance implements AutoCloseable {

    private static final System.Logger log = LogUtil.loggerFor(VulkanInstance.class);
    protected VkInstance instance;
    protected Set<String> requiredExtensions;
    protected List<VkPhysicalDevice> gpus;

    public VulkanInstance(VkInstance instance){
        this.instance = instance;
        requiredExtensions = new HashSet<>();
        gpus = new ArrayList<>();
    }

    public VkInstance getInstance(){
        return instance;
    }

    public List<VkPhysicalDevice> getGpus(){
        if(gpus.size() > 0){
            return gpus;
        }
        try(var stack = MemoryStack.stackPush()){

            var count = stack.callocInt(1);
            check(vkEnumeratePhysicalDevices(instance, count, null));

            if(count.get(0) > 0){
                var gpuCount = count.get(0);
                var deviceHandles = stack.callocPointer(gpuCount);
                check(vkEnumeratePhysicalDevices(instance, count, deviceHandles));
                for(int i = 0; i < count.get(0); i++){
                    gpus.add(new VkPhysicalDevice(deviceHandles.get(i), instance));
                }
            }
        }
        return gpus;
    }



    @Override
    public void close() {
        if(instance != null){
            vkDestroyInstance(instance, null);
            instance = null;
        }
    }

    public static VulkanInstance create(String engine, String renderer) {
        log.log(Level.INFO, "Creating Vulkan instance");
        try (var stack = MemoryStack.stackPush()) {

            var requiredExtensions = glfwGetRequiredInstanceExtensions();
            if (requiredExtensions == null) {
                throw new VulkanInitException("Could not query required Vulkan extensions");
            }

            var appName = stack.UTF8(engine);
            var rendererName = stack.UTF8(renderer);

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
                    .ppEnabledExtensionNames(requiredExtensions);

            var instancePtr = MemoryUtil.memCallocPointer(1);
            var result = vkCreateInstance(instanceInfo, null, instancePtr);
            if (result == VK_ERROR_INCOMPATIBLE_DRIVER) {
                throw new VulkanInitException("Cannot find a compatible Vulkan installable client driver");
            } else if (result == VK_ERROR_EXTENSION_NOT_PRESENT) {
                throw new VulkanInitException("This Vulkan driver does not have all required extensions");
            } else if (result != VK_SUCCESS) {
                throw VulkanInitException.forError(result, "Could not create Vulkan instance");
            }

            log.log(Level.INFO, "Successfully created Vulkan instance");

            var instance = new VulkanInstance(new VkInstance(instancePtr.get(0), instanceInfo));
            instance.requiredExtensions.addAll(Arrays.asList(VkUtil.fromASCII(requiredExtensions)));
            return instance;
        }
    }
}
