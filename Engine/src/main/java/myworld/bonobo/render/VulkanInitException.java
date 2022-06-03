package myworld.bonobo.render;

public class VulkanInitException extends RuntimeException {

    protected final int errCode;

    public VulkanInitException(String msg){
        this(0, msg);
    }

    public VulkanInitException(int errCode, String msg){
        super(msg);
        this.errCode = errCode;
    }

    public int getErrorCode(){
        return errCode;
    }

    public static VulkanInitException forError(int errCode, String msg){
        return new VulkanInitException(errCode, "Vulkan error 0x%X: %s".formatted(errCode, msg));
    }

    public static VulkanInitException forError(int errCode){
        return new VulkanInitException(errCode, "Vulkan error 0x%X".formatted(errCode));
    }
}
