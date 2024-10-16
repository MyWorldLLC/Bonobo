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

import myworld.bonobo.math.BMath;
import myworld.bonobo.platform.windowing.Window;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import static myworld.bonobo.platform.render.VkUtil.check;
import static myworld.bonobo.platform.render.VkUtil.firstMatch;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Surface implements AutoCloseable {

    protected final long handle;
    protected final Window window;
    protected final PhysicalDevice gpu;
    protected final RenderingDevice device;
    protected final int queueFamilyIndex;

    protected VkQueue queue;

    protected VkSurfaceFormatKHR.Buffer formats;

    protected VkSurfaceCapabilitiesKHR capabilities;

    protected VkExtent2D swapExtents;

    protected int[] presentModes;

    protected long swapchainHandle;

    protected long[] swapchainImages;
    protected long[] swapchainImageViews;

    protected int swapchainImageFormat;

    public Surface(long handle, Window window, PhysicalDevice gpu, RenderingDevice device, int queueFamilyIndex){
        this.handle = handle;
        this.window = window;
        this.gpu = gpu;
        this.device = device;
        this.queueFamilyIndex = queueFamilyIndex;
        swapchainHandle = VK_NULL_HANDLE;
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

                formats = VkSurfaceFormatKHR.calloc(count.get(0));
                check(vkGetPhysicalDeviceSurfaceFormatsKHR(gpu.getDevice(), handle, count, formats));
            }
        }
        return formats;
    }

    public VkSurfaceCapabilitiesKHR getCapabilities(){
        if(capabilities == null){
            capabilities = VkSurfaceCapabilitiesKHR.calloc();
            check(vkGetPhysicalDeviceSurfaceCapabilitiesKHR(gpu.getDevice(), handle, capabilities));
        }
        return capabilities;
    }

    public int[] getSupportedPresentModes(){
        if(presentModes == null){
            try(var stack = MemoryStack.stackPush()){
                var count = stack.callocInt(1);
                vkGetPhysicalDeviceSurfacePresentModesKHR(gpu.getDevice(), handle, count, null);

                var modes = stack.callocInt(count.get(0));
                vkGetPhysicalDeviceSurfacePresentModesKHR(gpu.getDevice(), handle, count, modes);

                presentModes = new int[count.get(0)];
                for(int i = 0; i < presentModes.length; i++){
                    presentModes[i] = modes.get(i);
                }
            }
        }
        return presentModes;
    }

    public VkExtent2D getSwapExtents(){
        if(swapExtents == null){
            var capabilities = getCapabilities();
            if(capabilities.currentExtent().width() != Integer.MAX_VALUE){
                return capabilities.currentExtent();
            }else{
                try(var stack = MemoryStack.stackPush()){
                    swapExtents = VkExtent2D.calloc();
                    var pWidth = stack.callocInt(1);
                    var pHeight = stack.callocInt(1);
                    glfwGetFramebufferSize(window.getHandle(), pWidth, pHeight);

                    swapExtents.set(
                            BMath.clamp(
                                    pWidth.get(0),
                                    capabilities.minImageExtent().width(),
                                    capabilities.maxImageExtent().width()),
                            BMath.clamp(
                                    pHeight.get(0),
                                    capabilities.minImageExtent().height(),
                                    capabilities.maxImageExtent().height()
                            ));
                }
            }
        }
        return swapExtents;
    }

    protected void createSwapChain(){
        var capabilities = getCapabilities();
        int imageCount = Math.min(capabilities.minImageCount() + 1, capabilities.maxImageCount());

        long oldSwapchain = swapchainHandle;

        try(var stack = MemoryStack.stackPush()){
            int surfaceFormat = getSupportedSurfaceFormats().format(); // TODO - should choose preferred surface
            int surfaceColorSpace = getSupportedSurfaceFormats().colorSpace(); // TODO - as above
            var createInfo = VkSwapchainCreateInfoKHR.calloc(stack)
                    .sType$Default()
                    .pNext(0)
                    .surface(handle)
                    .minImageCount(imageCount)
                    .imageFormat(surfaceFormat)
                    .imageColorSpace(surfaceColorSpace)
                    .imageExtent(getSwapExtents())
                    .imageArrayLayers(1)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT) // TODO - this may not be the final usage. It will probably become transfer_dst when postprocessing is supported
                    .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE) // TODO - if graphics/present queues aren't the same this will need to be concurrent.
                    .preTransform(capabilities.currentTransform())
                    .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR) // TODO - change this to allow window transparency
                    .presentMode(
                            VkUtil.contains(VK_PRESENT_MODE_MAILBOX_KHR, getSupportedPresentModes())
                                    ? VK_PRESENT_MODE_MAILBOX_KHR
                                    : VK_PRESENT_MODE_FIFO_KHR) // Only VK_PRESENT_MODE_FIFO_KHR is guaranteed to be available
                    .clipped(true)
                    .oldSwapchain(oldSwapchain);

            var pSwapchainHandle = stack.callocLong(1);
            check(vkCreateSwapchainKHR(device.getDevice(), createInfo, null, pSwapchainHandle));
            swapchainHandle = pSwapchainHandle.get(0);

            var createdImageCount = stack.callocInt(1);
            check(vkGetSwapchainImagesKHR(device.getDevice(), swapchainHandle, createdImageCount, null));

            var images = stack.callocLong(createdImageCount.get(0));
            check(vkGetSwapchainImagesKHR(device.getDevice(), swapchainHandle, createdImageCount, images));

            swapchainImages = new long[createdImageCount.get(0)];
            for(int i = 0; i < swapchainImages.length; i++){
                swapchainImages[i] = images.get(i);
            }
            swapchainImageFormat = surfaceFormat;

            swapchainImageViews = new long[swapchainImages.length];
            for(int i = 0; i < swapchainImageViews.length; i++){
                var imageViewCreateInfo = VkImageViewCreateInfo.calloc(stack)
                        .sType$Default()
                        .pNext(0)
                        .image(swapchainImages[i])
                        .viewType(VK_IMAGE_VIEW_TYPE_2D)
                        .format(swapchainImageFormat)
                        .components(c -> c.r(VK_COMPONENT_SWIZZLE_IDENTITY)
                                .b(VK_COMPONENT_SWIZZLE_IDENTITY)
                                .g(VK_COMPONENT_SWIZZLE_IDENTITY)
                                .a(VK_COMPONENT_SWIZZLE_IDENTITY)
                        )
                        .subresourceRange(r -> r.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                                .baseMipLevel(0)
                                .levelCount(1)
                                .baseArrayLayer(0)
                                .layerCount(1)
                        );

                var pImageView = stack.callocLong(1);
                check(vkCreateImageView(device.getDevice(), imageViewCreateInfo, null, pImageView));

                swapchainImageViews[i] = pImageView.get(0);
            }
        }
    }

    @Override
    public void close(){

        for(int i = 0; i < swapchainImageViews.length; i++){
            vkDestroyImageView(device.getDevice(), swapchainImageViews[i], null);
        }

        if(swapchainHandle != VK_NULL_HANDLE){
            vkDestroySwapchainKHR(device.getDevice(), swapchainHandle, null);
        }

        VkUtil.freeAll(
                formats,
                capabilities,
                swapExtents);
    }
}
