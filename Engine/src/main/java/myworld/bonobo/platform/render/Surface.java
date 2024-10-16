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
import myworld.bonobo.util.log.Logger;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.io.IOException;

import static myworld.bonobo.platform.render.VkUtil.check;
import static myworld.bonobo.platform.render.VkUtil.firstMatch;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Surface implements AutoCloseable {

    private static final Logger log = Logger.loggerFor(Surface.class);

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

    protected long renderPass;
    protected long pipelineLayout;
    protected long graphicsPipeline;

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
        int imageCount = capabilities.minImageCount() + 1;

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

    protected void createRenderPass(){

        try(var stack = MemoryStack.stackPush()){

            var colorAttachments = VkAttachmentDescription.calloc(1, stack);
            var colorAttachment = colorAttachments.get(0);
            colorAttachment.format(swapchainImageFormat);
            colorAttachment.samples(VK_SAMPLE_COUNT_1_BIT);
            colorAttachment.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
            colorAttachment.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
            colorAttachment.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
            colorAttachment.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            colorAttachment.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            colorAttachment.finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

            var colorAttachmentRefs = VkAttachmentReference.calloc(1, stack);
            var colorAttachmentRef = colorAttachmentRefs.get(0);
            colorAttachmentRef.attachment(0);
            colorAttachmentRef.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            var subpasses = VkSubpassDescription.calloc(1, stack);
            var subpass = subpasses.get(0);
            subpass.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
            subpass.colorAttachmentCount(1);
            subpass.pColorAttachments(colorAttachmentRefs);

            var renderPassInfo = VkRenderPassCreateInfo.calloc(stack);
            renderPassInfo.sType$Default();
            renderPassInfo.pAttachments(colorAttachments);
            renderPassInfo.pSubpasses(subpasses);

            var pRenderPass = stack.callocLong(1);
            check(vkCreateRenderPass(device.getDevice(), renderPassInfo, null, pRenderPass));
            renderPass = pRenderPass.get();
        }
    }

    protected void createGraphicsPipeline(){

        var vertShader = createShaderModule("/vert.spv");
        var fragShader = createShaderModule("/frag.spv");

        try(var stack = MemoryStack.stackPush()){

            var shaderStages = VkPipelineShaderStageCreateInfo.calloc(2, stack);
            var vertShaderStageInfo = shaderStages.get(0);
            vertShaderStageInfo.sType$Default();
            vertShaderStageInfo.stage(VK_SHADER_STAGE_VERTEX_BIT);
            vertShaderStageInfo.module(vertShader);
            vertShaderStageInfo.pName(stack.ASCII("main"));

            var fragShaderStageInfo = shaderStages.get(1);
            fragShaderStageInfo.sType$Default();
            fragShaderStageInfo.stage(VK_SHADER_STAGE_FRAGMENT_BIT);
            fragShaderStageInfo.module(fragShader);
            fragShaderStageInfo.pName(stack.ASCII("main"));

            var dynamicStates = new int[]{VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR};

            var dynamicState = VkPipelineDynamicStateCreateInfo.calloc(stack);
            dynamicState.sType$Default();
            dynamicState.pDynamicStates(stack.ints(dynamicStates));

            var vertexInputInfo = VkPipelineVertexInputStateCreateInfo.calloc(stack);
            vertexInputInfo.sType$Default();
            vertexInputInfo.pVertexBindingDescriptions(null);
            vertexInputInfo.pVertexAttributeDescriptions(null);

            var inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc(stack);
            inputAssembly.sType$Default();
            inputAssembly.topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
            inputAssembly.primitiveRestartEnable(false);

            var swapExtents = getSwapExtents();
            var viewport = VkViewport.calloc(stack);
            viewport.x(0f);
            viewport.y(0f);
            viewport.width(swapExtents.width());
            viewport.height(swapExtents.height());
            viewport.minDepth(0f);
            viewport.maxDepth(1f);

            var scissor = VkRect2D.calloc(stack);
            scissor.offset().set(0, 0);
            scissor.extent().set(swapExtents);

            var viewportState = VkPipelineViewportStateCreateInfo.calloc(stack);
            viewportState.sType$Default();
            viewportState.viewportCount(1);
            viewportState.scissorCount(1);

            var rasterizer = VkPipelineRasterizationStateCreateInfo.calloc(stack);
            rasterizer.sType$Default();
            rasterizer.depthClampEnable(false);
            rasterizer.rasterizerDiscardEnable(false);
            rasterizer.polygonMode(VK_POLYGON_MODE_FILL);
            rasterizer.lineWidth(1.0f);
            rasterizer.cullMode(VK_CULL_MODE_BACK_BIT);
            rasterizer.frontFace(VK_FRONT_FACE_CLOCKWISE);
            rasterizer.depthBiasEnable(false);
            rasterizer.depthBiasConstantFactor(0f);
            rasterizer.depthBiasClamp(0f);
            rasterizer.depthBiasSlopeFactor(0f);

            var multisampling = VkPipelineMultisampleStateCreateInfo.calloc(stack);
            multisampling.sType$Default();
            multisampling.sampleShadingEnable(false);
            multisampling.rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);
            multisampling.minSampleShading(1f);
            multisampling.pSampleMask(null);
            multisampling.alphaToCoverageEnable(false);
            multisampling.alphaToOneEnable(false);

            var colorBlendAttachments = VkPipelineColorBlendAttachmentState.calloc(1, stack);

            var colorBlendAttachment = colorBlendAttachments.get(0);
            colorBlendAttachment.colorWriteMask(
                    VK_COLOR_COMPONENT_R_BIT |
                          VK_COLOR_COMPONENT_G_BIT |
                          VK_COLOR_COMPONENT_B_BIT |
                          VK_COLOR_COMPONENT_A_BIT
            );
            colorBlendAttachment.blendEnable(false);
            colorBlendAttachment.srcColorBlendFactor(VK_BLEND_FACTOR_ONE);
            colorBlendAttachment.dstColorBlendFactor(VK_BLEND_FACTOR_ZERO);
            colorBlendAttachment.colorBlendOp(VK_BLEND_OP_ADD);
            colorBlendAttachment.srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE);
            colorBlendAttachment.dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO);
            colorBlendAttachment.alphaBlendOp(VK_BLEND_OP_ADD);

            var colorBlending = VkPipelineColorBlendStateCreateInfo.calloc(stack);
            colorBlending.sType$Default();
            colorBlending.logicOpEnable(false);
            colorBlending.logicOp(VK_LOGIC_OP_COPY);
            colorBlending.pAttachments(colorBlendAttachments);
            colorBlending.blendConstants(stack.floats(
                    0f,
                    0f,
                    0f,
                    0f
            ));

            var pipelineLayoutInfo = VkPipelineLayoutCreateInfo.calloc(stack);
            pipelineLayoutInfo.sType$Default();
            pipelineLayoutInfo.pSetLayouts(null);
            pipelineLayoutInfo.pPushConstantRanges(null);

            var pPipelineLayout = stack.callocLong(1);
            check(vkCreatePipelineLayout(device.getDevice(), pipelineLayoutInfo, null, pPipelineLayout));
            pipelineLayout = pPipelineLayout.get();

            var pipelineInfos = VkGraphicsPipelineCreateInfo.calloc(1, stack);
            var pipelineInfo = pipelineInfos.get(0);
            pipelineInfo.sType$Default();
            pipelineInfo.pStages(shaderStages);
            pipelineInfo.pVertexInputState(vertexInputInfo);
            pipelineInfo.pInputAssemblyState(inputAssembly);
            pipelineInfo.pViewportState(viewportState);
            pipelineInfo.pRasterizationState(rasterizer);
            pipelineInfo.pMultisampleState(multisampling);
            pipelineInfo.pDepthStencilState(null);
            pipelineInfo.pColorBlendState(colorBlending);
            pipelineInfo.pDynamicState(dynamicState);
            pipelineInfo.layout(pipelineLayout);
            pipelineInfo.renderPass(renderPass);
            pipelineInfo.subpass(0);
            pipelineInfo.basePipelineHandle(VK_NULL_HANDLE);
            pipelineInfo.basePipelineIndex(-1);

            var pGraphicsPipeline = stack.callocLong(1);
            check(vkCreateGraphicsPipelines(device.getDevice(), VK_NULL_HANDLE, pipelineInfos, null, pGraphicsPipeline));
            graphicsPipeline = pGraphicsPipeline.get();
        }

        destroyShaderModule(vertShader);
        destroyShaderModule(fragShader);
    }

    protected long createShaderModule(String resource){

        try(var code = getClass().getResourceAsStream(resource)){
            if(code == null){
                log.error("No such resource: %s", resource);
                return 0;
            }

            var codeBytes = code.readAllBytes();

            try(var stack = MemoryStack.stackPush()){

                var codeBuf = stack.bytes(codeBytes);

                var createInfo = VkShaderModuleCreateInfo.calloc(stack);
                createInfo.sType$Default();
                createInfo.pNext(0);
                createInfo.pCode(codeBuf);

                var moduleHandle = stack.callocLong(1);
                check(vkCreateShaderModule(device.getDevice(), createInfo, null, moduleHandle));
                return moduleHandle.get();
            }
        }catch(IOException e){
            log.error("Failed to load shader %s: %s", resource, e.getMessage());
            return 0;
        }
    }

    protected void destroyShaderModule(long shaderModule){
        if(shaderModule != 0){
            vkDestroyShaderModule(device.getDevice(), shaderModule, null);
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

        if(graphicsPipeline != VK_NULL_HANDLE){
            vkDestroyPipeline(device.getDevice(), graphicsPipeline, null);
        }

        if(pipelineLayout != VK_NULL_HANDLE){
            vkDestroyPipelineLayout(device.getDevice(), pipelineLayout, null);
        }

        if(renderPass != VK_NULL_HANDLE){
            vkDestroyRenderPass(device.getDevice(), renderPass, null);
        }

        VkUtil.freeAll(
                formats,
                capabilities,
                swapExtents);
    }
}
