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

import static myworld.bonobo.render.VkUtil.firstMatch;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.VK10.*;

import myworld.bonobo.core.AppSystem;
import myworld.bonobo.core.Application;
import myworld.bonobo.platform.GlfwWindowSystem;
import myworld.bonobo.platform.Window;
import myworld.bonobo.util.ResourceScope;
import myworld.bonobo.util.log.Logger;

import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VulkanRenderSystem extends AppSystem {

    public static final String ENGINE_NAME = "Bonobo";
    public static final String RENDERER_NAME = "Bonobo VK";

    private static final Logger log = Logger.loggerFor(VulkanRenderSystem.class);
    protected final Application app;

    protected Instance instance;
    protected PhysicalDevice gpu;

    protected final List<Surface> surfaces;

    public VulkanRenderSystem(Application app){
        this.app = app;
        surfaces = new ArrayList<>();
    }

    @Override
    public void initialize(){

        // If used with the default windowing implementation, this will already have happened.
        // GLFW allows multiple init calls, and subsequent calls (following a successful init)
        // have no effect.
        if(!glfwInit()){
            log.error("Could not initialize GLFW, exiting");
            app.stop();
        }

        if(!glfwVulkanSupported()){
            log.error("Vulkan is not supported on this device, exiting");
            app.stop();
        }

        log.info("Initializing Vulkan renderer");

        instance = Instance.create(ENGINE_NAME, RENDERER_NAME);

        // Use only an integrated or discrete gpu, and prefer discrete gpus to integrated
        // TODO - we need a much more intelligent way to choose the device, and it needs
        // to be able to be overridden via configuration
        var gpus = instance.getGpus().stream()
                .filter(d -> {
                    int type = d.getProperties().deviceType();
                    return type == VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU
                            || type == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU;
                }).sorted(
                        Comparator.comparingInt((PhysicalDevice d) -> d.getProperties().deviceType())
                                .reversed())
                .toList();

        gpus.forEach(gpu -> {
            gpu.getProperties();
            log.info("Found GPU: %s", gpu.getProperties().deviceNameString());
        });

        if (gpus.isEmpty()) {
            log.error("No suitable GPU was found on this system, exiting");
            app.stop();
        }

        gpu = gpus.get(0);

        createWindowSurface(app.getSystem(GlfwWindowSystem.class)
                .getWindow(GlfwWindowSystem.FIRST_WINDOW_ID));

    }

    public void createWindowSurface(Window window){
        try(var stack = MemoryStack.stackPush()){

            var surfaceHandle = stack.callocLong(1);
            glfwCreateWindowSurface(instance.getInstance(), window.getHandle(), null, surfaceHandle);
            window.setSurfaceHandle(surfaceHandle.get(0));

            var queueFamilyIndex = firstMatch(
                    gpu.getQueueFamilyPresentationSupport(surfaceHandle.get(0)),
                    gpu.getQueueFamilySupport(VK_QUEUE_GRAPHICS_BIT),
                    true);

            if(queueFamilyIndex == -1){
                throw new VulkanException("Cannot find a GPU that supports both graphics & presentation");
            }

            var device = RenderingDevice.create(gpu, queueFamilyIndex);
            var surface = new Surface(surfaceHandle.get(0), window, gpu, device, queueFamilyIndex);
            surface.createSwapChain();

            surfaces.add(surface);

        }
    }

    public void destroySurface(long handle){
        var maybeSurface = surfaces.stream()
                .filter(s -> s.getHandle() == handle)
                .findFirst();

        if(maybeSurface.isPresent()){
            var surface = maybeSurface.get();
            surface.close();
            // Surface does not close its own device, as in the future devices may be shared between surfaces
            surface.getDevice().close();
            vkDestroySurfaceKHR(instance.getInstance(), maybeSurface.get().getHandle(), null);
        }
        surfaces.removeIf(s -> s.getHandle() == handle);
    }

    public Instance getInstance(){
        return instance;
    }

    @Override
    public void stop() {
        try{
            // Note: surfaces must be closed before the instance is closed
            surfaces.forEach(s -> {
                vkDestroySurfaceKHR(instance.getInstance(), s.getHandle(), null);
            });
            instance.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
