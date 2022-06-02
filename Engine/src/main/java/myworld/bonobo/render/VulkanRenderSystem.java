package myworld.bonobo.render;

import static java.lang.System.Logger.Level;
import myworld.bonobo.core.AppSystem;
import myworld.bonobo.util.LogUtil;
import org.lwjgl.vulkan.VkAllocationCallbacks;

public class VulkanRenderSystem extends AppSystem {

    private static final System.Logger log = LogUtil.loggerFor(VulkanRenderSystem.class);

    @Override
    public void initialize(){
        log.log(Level.INFO, "Initializing Vulkan renderer");
        var allocCallbacks = VkAllocationCallbacks.calloc();
        // TODO
        
    }

    @Override
    public void stop(){

    }
}
