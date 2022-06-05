package myworld.bonobo.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;

import static myworld.bonobo.render.VkUtil.check;
import static org.lwjgl.vulkan.VK10.vkCreateDevice;

public class RenderingDevice implements AutoCloseable {

    protected final VkDevice device;
    public RenderingDevice(VkDevice device){
        this.device = device;
    }

    public VkDevice getDevice(){
        return device;
    }

    public static RenderingDevice create(PhysicalDevice gpu, int queueFamilyIndex){
        try(var stack = MemoryStack.stackPush()){
            var queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1, stack)
                    .sType$Default()
                    .pNext(0)
                    .flags(0)
                    .queueFamilyIndex(queueFamilyIndex)
                    .pQueuePriorities(stack.floats(0f));

            var deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            if(gpu.getFeatures().shaderClipDistance()){
                deviceFeatures.shaderClipDistance(true);
            }

            var deviceCreateInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType$Default()
                    .pNext(0)
                    .flags(0)
                    .pQueueCreateInfos(queueCreateInfo)
                    .ppEnabledLayerNames(null)
                    .ppEnabledExtensionNames(VkUtil.toAscii(stack, gpu.getExtensions()))
                    .pEnabledFeatures(deviceFeatures);

            var handlePtr = stack.callocPointer(1);
            check(vkCreateDevice(gpu.getDevice(), deviceCreateInfo, null, handlePtr));
            return new RenderingDevice(new VkDevice(handlePtr.get(0), gpu.getDevice(), deviceCreateInfo));
        }
    }

    @Override
    public void close(){}
}
