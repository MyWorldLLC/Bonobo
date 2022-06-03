package myworld.bonobo.render;

public class VkErrUtil {

    public static void check(int errCode) throws VulkanInitException {
        if(errCode != 0){
            throw VulkanInitException.forError(errCode);
        }
    }

    public static void check(int errCode, String msg) throws VulkanInitException {
        if(errCode != 0){
            throw VulkanInitException.forError(errCode, msg);
        }
    }

}
