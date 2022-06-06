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

import myworld.bonobo.util.log.Logger;
import static java.lang.System.Logger.Level;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.*;

import static myworld.bonobo.render.VkUtil.check;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class Instance implements AutoCloseable {

    private static final Logger log = Logger.loggerFor(Instance.class);
    protected VkInstance instance;
    protected Set<String> requiredExtensions;
    protected List<PhysicalDevice> gpus;

    public Instance(VkInstance instance){
        this.instance = instance;
        requiredExtensions = new HashSet<>();
        gpus = new ArrayList<>();
    }

    public VkInstance getInstance(){
        return instance;
    }

    public List<PhysicalDevice> getGpus(){
        if(gpus.size() > 0){
            return gpus;
        }
        try(var stack = MemoryStack.stackPush()){

            var count = stack.callocInt(1);
            check(vkEnumeratePhysicalDevices(instance, count, null));
            log.debug("System reports %d devices", count.get(0));

            if(count.get(0) > 0){
                var gpuCount = count.get(0);
                var deviceHandles = stack.callocPointer(gpuCount);
                check(vkEnumeratePhysicalDevices(instance, count, deviceHandles));
                for(int i = 0; i < gpuCount; i++){
                    gpus.add(new PhysicalDevice(this, new VkPhysicalDevice(deviceHandles.get(i), instance)));
                }
            }
        }
        return new ArrayList<>(gpus);
    }



    @Override
    public void close() throws Exception {
        VkUtil.closeAll(gpus);
        if(instance != null){
            vkDestroyInstance(instance, null);
            instance = null;
        }
    }
    public static Instance create(String engine, String renderer) {
        log.log(Level.INFO, "Creating Vulkan instance");
        try (var stack = MemoryStack.stackPush()) {

            var requiredExtensions = glfwGetRequiredInstanceExtensions();
            if (requiredExtensions == null) {
                throw new VulkanException("Could not query required Vulkan extensions");
            }

            PointerBuffer enabledLayers = null;
            // TODO - only do this for debug configuration
            var availableLayerCount = stack.callocInt(1);
            vkEnumerateInstanceLayerProperties(availableLayerCount, null);
            var availableLayers = VkLayerProperties.calloc(availableLayerCount.get(0), stack);
            vkEnumerateInstanceLayerProperties(availableLayerCount, availableLayers);

            log.info("%d validation layers available", availableLayerCount.get(0));

            for(int i = 0; i < availableLayerCount.get(0); i++){
                log.info("Available validation layer: %s", availableLayers.get(i).layerNameString());
                if(availableLayers.get(i).layerNameString().equals("VK_LAYER_KHRONOS_validation")){
                    enabledLayers = stack.callocPointer(1);
                    enabledLayers.put(0, stack.ASCII("VK_LAYER_KHRONOS_validation"));
                    log.info("Enabling validation layers");
                }
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
                    .ppEnabledLayerNames(enabledLayers)
                    .ppEnabledExtensionNames(requiredExtensions);

            var instancePtr = MemoryUtil.memCallocPointer(1);
            var result = vkCreateInstance(instanceInfo, null, instancePtr);
            if (result == VK_ERROR_INCOMPATIBLE_DRIVER) {
                throw new VulkanException("Cannot find a compatible Vulkan installable client driver");
            } else if (result == VK_ERROR_EXTENSION_NOT_PRESENT) {
                throw new VulkanException("This Vulkan driver does not have all required extensions");
            } else if (result != VK_SUCCESS) {
                throw VulkanException.forError(result, "Could not create Vulkan instance");
            }

            log.log(Level.INFO, "Successfully created Vulkan instance");

            var instance = new Instance(new VkInstance(instancePtr.get(0), instanceInfo));
            instance.requiredExtensions.addAll(Arrays.asList(VkUtil.fromASCII(requiredExtensions)));
            return instance;
        }
    }
}
