package myworld.bonobo.render;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.util.Collection;

public class VkUtil {

    public static String[] fromASCII(PointerBuffer strArray){
        String[] strings = new String[strArray.capacity()];
        for(int i = 0; i < strArray.capacity(); i++){
            strings[i] = strArray.getStringASCII(i);
        }
        return strings;
    }

    public static PointerBuffer toAscii(MemoryStack stack, String[] strings){
        var buf = stack.callocPointer(strings.length);
        for(int i = 0; i < strings.length; i++){
            var ascii = stack.ASCII(strings[i], true);
            buf.put(i, ascii);
        }
        return buf;
    }

    public static void closeAll(AutoCloseable... closeables) throws Exception {
        for(var closeable : closeables){
            closeable.close();
        }
    }

    public static void closeAll(Collection<? extends AutoCloseable> closeables) throws Exception {
        for(var closeable : closeables){
            closeable.close();
        }
    }

}
