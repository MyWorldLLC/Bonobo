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

import static myworld.bonobo.platform.render.VkUtil.firstMatch;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_NO_API;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.VK10.*;

import myworld.bonobo.core.AppSystem;
import myworld.bonobo.core.Application;
import myworld.bonobo.platform.PlatformSystem;
import myworld.bonobo.platform.windowing.Window;
import myworld.bonobo.platform.windowing.WindowFeatures;
import myworld.bonobo.platform.windowing.WindowManager;
import myworld.bonobo.util.log.Logger;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GlfwVulkanPlatform extends AppSystem implements PlatformSystem {

    public static final String ENGINE_NAME = "Bonobo";
    public static final String RENDERER_NAME = "Bonobo VK";

    private static final Logger log = Logger.loggerFor(GlfwVulkanPlatform.class);
    protected final Application app;
    protected GLFWErrorCallback errorCallback;
    protected final WindowManager<VulkanWindow> windows;
    protected GLFWWindowSizeCallback sizeCallback;

    protected Instance instance;
    protected PhysicalDevice gpu;

    protected final List<Surface> surfaces;

    public GlfwVulkanPlatform(Application app){
        this.app = app;
        windows = new WindowManager<>(this::createVKWindow);
        surfaces = new ArrayList<>();
    }

    public Window createWindow(WindowFeatures features){
        var window = windows.createWindow(features);
        if(window == null){
            log.warning("Failed to create Vulkan window");
            return null;
        }
        createWindowSurface(window);
        return window;
    }

    protected VulkanWindow createVKWindow(int id, WindowFeatures features){

        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_VISIBLE, features.state().startVisible() ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_DECORATED, features.state().borderless() ? GLFW_FALSE : GLFW_TRUE);

        var handle = glfwCreateWindow(features.width(), features.height(), features.title(), NULL, NULL);
        if(handle == NULL){
            return null;
        }

        glfwSetWindowSizeCallback(handle, sizeCallback);

        return new VulkanWindow(id, handle);
    }

    @Override
    public void initialize(){
        log.info("Running on LWJGL " + Version.getVersion());

        errorCallback = GLFWErrorCallback.createPrint(System.err).set();
        sizeCallback = GLFWWindowSizeCallback.create(this::onResize);

        glfwSetErrorCallback(errorCallback);
        if(!glfwInit()){
            log.error( "Could not initialize GLFW, exiting");
            app.stop();
        }
        log.log(System.Logger.Level.INFO, "Initialized GLFW successfully");

        if(!glfwVulkanSupported()){
            log.error("Vulkan is not supported on this device, exiting");
            app.stop();
        }

        log.info("Initializing Vulkan renderer");

        instance = Instance.create(ENGINE_NAME, RENDERER_NAME);

        // Sort by physical device type.
        // TODO - need to support overriding this via configuration
        var gpus = instance.getGpus().stream()
                .sorted(
                        Comparator.comparingInt((PhysicalDevice d) -> deviceSortKey(d.getProperties().deviceType())))
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
        log.info("Using GPU: %s", gpu.getProperties().deviceNameString());

    }

    public void createWindowSurface(VulkanWindow window){
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
            surface.createRenderPass();
            surface.createGraphicsPipeline();
            System.out.println("Surface created");

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
    public void update(double tpf){
        glfwPollEvents();
        windows.getWindows().removeIf(this::closeIfRequested);
        if(windows.getWindows().isEmpty()){
            log.info("All windows closed, exiting");
            app.stop();
        }
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
        glfwTerminate();
    }

    protected boolean closeIfRequested(VulkanWindow window){
        if(glfwWindowShouldClose(window.getHandle())){
            closeWindow(window);
            return true;
        }
        return false;
    }

    protected void closeWindow(VulkanWindow window){
        if(window.hasSurface()){
            destroySurface(window.getSurfaceHandle());
        }
        glfwDestroyWindow(window.getHandle());
    }

    @Override
    public WindowManager<VulkanWindow> getWindowManager() {
        return windows;
    }

    protected static int deviceSortKey(int deviceType){
        var deviceTypes = new int[]{
                VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU,
                VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU,
                VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU,
                VK_PHYSICAL_DEVICE_TYPE_CPU,
                VK_PHYSICAL_DEVICE_TYPE_OTHER
        };

        for(int i = 0; i < deviceTypes.length; i++){
            if(deviceTypes[i] == deviceType){
                return i;
            }
        }

        return Integer.MAX_VALUE;
    }

    protected void onResize(long windowHandle, int width, int height){
        var window = windows.getWindows()
                .stream()
                .filter(w -> w.getHandle() == windowHandle)
                .findFirst();

        if(window.isEmpty()){
            return;
        }

        destroySurface(window.get().getSurfaceHandle());
        createWindowSurface(window.get());
    }
}
