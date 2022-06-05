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

import myworld.bonobo.platform.Window;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import static myworld.bonobo.render.VkUtil.check;
import static myworld.bonobo.render.VkUtil.firstMatch;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.VK10.*;

public class Surface implements AutoCloseable {

    protected final long handle;
    protected final Window window;
    protected final PhysicalDevice gpu;
    protected final RenderingDevice device;
    protected final int queueFamilyIndex;

    protected VkQueue queue;

    protected VkSurfaceFormatKHR.Buffer formats;

    public Surface(long handle, Window window, PhysicalDevice gpu, RenderingDevice device, int queueFamilyIndex){
        this.handle = handle;
        this.window = window;
        this.gpu = gpu;
        this.device = device;
        this.queueFamilyIndex = queueFamilyIndex;
    }

    public long getHandle() {
        return handle;
    }

    public Window getWindow() {
        return window;
    }

    public PhysicalDevice getGpu(){
        return gpu;
    }

    public RenderingDevice getDevice() {
        return device;
    }

    public int getQueueFamilyIndex() {
        return queueFamilyIndex;
    }

    public VkQueue getQueue(){
        if(queue == null){
            try(var stack = MemoryStack.stackPush()){
                var queuePtr = stack.callocPointer(1);
                vkGetDeviceQueue(device.getDevice(), queueFamilyIndex, firstMatch(
                        gpu.getQueueFamilyPresentationSupport(handle),
                        gpu.getQueueFamilySupport(VK_QUEUE_GRAPHICS_BIT),
                        true), queuePtr);

                queue = new VkQueue(queuePtr.get(0), device.getDevice());
            }
        }
        return queue;
    }

    public VkSurfaceFormatKHR.Buffer getSupportedSurfaceFormats(){
        if(formats == null){
            try(var stack = MemoryStack.stackPush()){
                var count = stack.callocInt(1);
                check(vkGetPhysicalDeviceSurfaceFormatsKHR(gpu.getDevice(), handle, count, null));

                var surfaceFormats = VkSurfaceFormatKHR.calloc(count.get(0));
                check(vkGetPhysicalDeviceSurfaceFormatsKHR(gpu.getDevice(), handle, count, surfaceFormats));
            }
        }
        return formats;
    }

    @Override
    public void close(){
        formats.close();
    }
}
