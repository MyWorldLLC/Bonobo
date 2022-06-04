package myworld.bonobo.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.*;

import static myworld.bonobo.render.VkErrUtil.check;
import static org.lwjgl.vulkan.VK10.*;

public class PhysicalDevice implements AutoCloseable {

    protected final Instance instance;
    protected final VkPhysicalDevice device;
    protected final Set<String> deviceExtensions;

    protected VkPhysicalDeviceProperties properties;

    protected VkQueueFamilyProperties.Buffer queueFamilyProperties;
    protected VkPhysicalDeviceFeatures features;

    public PhysicalDevice(Instance instance, VkPhysicalDevice device){
        this.instance = instance;
        this.device = device;
        deviceExtensions = new HashSet<>();
    }

    public Instance getInstance(){
        return instance;
    }

    public VkPhysicalDevice getDevice(){
        return device;
    }

    public Set<String> getExtensions(){
        if(deviceExtensions.isEmpty()){
            try(var stack = MemoryStack.stackPush()){
                var count = stack.callocInt(1);
                check(vkEnumerateDeviceExtensionProperties(device, (String)null, count, null));

                var deviceExtensions = VkExtensionProperties.calloc(count.get(0), stack);
                check(vkEnumerateDeviceExtensionProperties(device, (String)null, count, deviceExtensions));
                for(int i = 0; i < count.get(0); i++){
                    deviceExtensions.position(i);
                    this.deviceExtensions.add(deviceExtensions.extensionNameString());
                }
            }
        }
        return Collections.unmodifiableSet(deviceExtensions);
    }

    public boolean hasExtension(String extension){
        return getExtensions().contains(extension);
    }

    public VkPhysicalDeviceProperties getProperties(){
        if(properties == null){
            properties = VkPhysicalDeviceProperties.calloc();
            vkGetPhysicalDeviceProperties(device, properties);
        }
        return properties;
    }

    public VkQueueFamilyProperties.Buffer getQueueFamilyProperties(){
        if(queueFamilyProperties == null){
            try(var stack = MemoryStack.stackPush()){

                var count = stack.callocInt(1);
                vkGetPhysicalDeviceQueueFamilyProperties(device, count, null);

                queueFamilyProperties = VkQueueFamilyProperties.calloc(count.get(0));
                vkGetPhysicalDeviceQueueFamilyProperties(device, count, queueFamilyProperties);
            }
        }
        return queueFamilyProperties;
    }

    public VkPhysicalDeviceFeatures getFeatures(){
        if(features == null){
            features = VkPhysicalDeviceFeatures.calloc();
            vkGetPhysicalDeviceFeatures(device, features);
        }
        return features;
    }

    @Override
    public void close(){
        if(properties != null){
            properties.free();
        }
        if(queueFamilyProperties != null){
            queueFamilyProperties.free();
        }
        if(features != null){
            features.free();
        }
    }
}
