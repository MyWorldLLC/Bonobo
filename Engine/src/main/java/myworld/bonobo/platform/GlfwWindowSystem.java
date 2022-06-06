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

package myworld.bonobo.platform;

import myworld.bonobo.core.AppSystem;
import myworld.bonobo.core.Application;
import myworld.bonobo.render.VulkanRenderSystem;
import myworld.bonobo.util.log.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.Logger.Level;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GlfwWindowSystem extends AppSystem {

    public static final int FIRST_WINDOW_ID = 1;

    private static final Logger log = Logger.loggerFor(GlfwWindowSystem.class);

    protected final Application application;

    protected GLFWErrorCallback errorCallback;
    protected final AtomicInteger windowIds;

    protected final List<Window> windows;

    public GlfwWindowSystem(Application application){
        this.application = application;
        windowIds = new AtomicInteger(FIRST_WINDOW_ID);
        windows = new ArrayList<>();
    }

    @Override
    public void initialize(){
        log.info("Running on LWJGL " + Version.getVersion());

        errorCallback = GLFWErrorCallback.createPrint(System.err).set();

        glfwSetErrorCallback(errorCallback);
        if(!glfwInit()){
            log.error( "Could not initialize GLFW, exiting");
            application.stop();
        }
        log.log(Level.INFO, "Initialized GLFW successfully");

        var window = createWindow("Bonobo", 640, 480);
        if (window == null) {
            log.error( "Failed to create a window, exiting");
            application.stop();
        }

    }

    @Override
    public void update(double tpf){
        glfwPollEvents();
        windows.removeIf(this::closeIfRequested);
        if(windows.isEmpty()){
            log.info("All windows closed, exiting");
            application.stop();
        }
    }

    @Override
    public void stop(){
        windows.forEach(this::closeWindow);
        windows.clear();
        //glfwTerminate();
    }

    public Window createWindow(String title, int width, int height){
        // Don't create an OpenGL context by default
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        var handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if(handle == NULL){
            return null;
        }
        var window = new Window(windowIds.getAndIncrement(), handle);
        windows.add(window);
        return window;
    }

    public List<Window> getWindows(){
        return Collections.unmodifiableList(windows);
    }

    public Window getWindow(int windowId){
        return windows.stream()
                .filter(w -> w.getId() == windowId)
                .findFirst()
                .get();
    }

    protected boolean closeIfRequested(Window window){
        if(glfwWindowShouldClose(window.getHandle())){
            closeWindow(window);
            return true;
        }
        return false;
    }

    protected void closeWindow(Window window){
        if(window.hasSurface()){
            application.getSystem(VulkanRenderSystem.class).destroySurface(window.getSurfaceHandle());
        }
        glfwDestroyWindow(window.getHandle());
    }
}
