package myworld.bonobo.platform.render;

import myworld.bonobo.platform.windowing.Window;

import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;

public class VulkanWindow extends Window {
    protected VulkanWindow(int id, long handle) {
        super(id, handle);
    }

    @Override
    public void setVisible(boolean visible){
        if(visible){
            glfwShowWindow(handle);
        }else{
            glfwHideWindow(handle);
        }
    }
}
