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

package myworld.bonobo.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.*;

import static myworld.bonobo.render.VkUtil.check;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.VK10.*;

public class PhysicalDevice implements AutoCloseable {

    protected final Instance instance;
    protected final VkPhysicalDevice device;
    protected final Set<String> deviceExtensions;

    protected VkPhysicalDeviceProperties properties;

    protected VkQueueFamilyProperties.Buffer queueFamilyProperties;
    protected VkPhysicalDeviceFeatures features;

    protected VkPhysicalDeviceMemoryProperties memoryProperties;

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

    public boolean[] getQueueFamilyPresentationSupport(long surface){
        try(var stack = MemoryStack.stackPush()){
            var supportFlag = stack.callocInt(1);
            boolean[] supports = new boolean[getQueueFamilyProperties().capacity()];
            for(int i = 0; i < supports.length; i++){
                check(vkGetPhysicalDeviceSurfaceSupportKHR(device, i, surface, supportFlag));
                supports[i] = supportFlag.get(0) == VK_TRUE;
            }
            return supports;
        }
    }

    public boolean[] getQueueFamilySupport(int flags){
        var props = getQueueFamilyProperties();
        boolean[] supports = new boolean[props.capacity()];
        for (int i = 0; i < supports.length; i++) {
            supports[i] = (props.get(i).queueFlags() & flags) != 0;
        }
        return supports;
    }

    public VkPhysicalDeviceMemoryProperties getMemoryProperties(){
        if(memoryProperties == null){
            memoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
            vkGetPhysicalDeviceMemoryProperties(device, memoryProperties);
        }
        return memoryProperties;
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
        if(memoryProperties != null){
            memoryProperties.free();
        }
    }
}
