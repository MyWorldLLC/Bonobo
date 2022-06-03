package myworld.bonobo.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.util.ArrayList;
import java.util.List;

import static myworld.bonobo.render.VkErrUtil.check;
import static org.lwjgl.vulkan.VK10.vkDestroyInstance;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;

public class VulkanInstance implements AutoCloseable {
    protected VkInstance instance;
    protected List<VkPhysicalDevice> gpus;

    public VulkanInstance(VkInstance instance){
        this.instance = instance;
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
}
