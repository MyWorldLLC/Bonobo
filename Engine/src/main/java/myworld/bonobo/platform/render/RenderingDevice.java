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

package myworld.bonobo.platform.render;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.List;

import static myworld.bonobo.platform.render.VkUtil.check;
import static org.lwjgl.vulkan.VK10.*;

public class RenderingDevice implements AutoCloseable {

    protected final VkDevice device;

    protected VkQueue queue;

    public RenderingDevice(VkDevice device){
        this.device = device;
    }

    public VkDevice getDevice(){
        return device;
    }

    public VkQueue getQueue(){
        if(queue == null){
            vkGetDeviceQueue(device, 0, 0, null);
        }
        return queue;
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
                    //.ppEnabledLayerNames(null)
                    //.ppEnabledExtensionNames(VkUtil.toAscii(stack, gpu.getExtensions()))
                    // TODO - this needs to be configurable. Right now just enable the swapchain extension,
                    // because it's always present on a VK instance that's capable of displaying a window.
                    // The problem with enabling all extensions is that they may have dependencies on instance
                    // extensions that are not enabled.
                    .ppEnabledExtensionNames(VkUtil.toAscii(stack, List.of("VK_KHR_swapchain")))
                    .pEnabledFeatures(deviceFeatures);

            var handlePtr = stack.callocPointer(1);
            check(vkCreateDevice(gpu.getDevice(), deviceCreateInfo, null, handlePtr));
            return new RenderingDevice(new VkDevice(handlePtr.get(0), gpu.getDevice(), deviceCreateInfo));
        }
    }

    @Override
    public void close(){
        vkDestroyDevice(device, null);
    }
}
