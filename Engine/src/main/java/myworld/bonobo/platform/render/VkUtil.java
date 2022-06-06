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

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeResource;

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

    public static PointerBuffer toAscii(MemoryStack stack, Collection<String> strings){
        return toAscii(stack, strings.toArray(new String[]{}));
    }

    public static void closeAll(AutoCloseable... closeables) throws Exception {
        for(var closeable : closeables){
            if(closeable != null){
                closeable.close();
            }
        }
    }

    public static void closeAll(Collection<? extends AutoCloseable> closeables) throws Exception {
        for(var closeable : closeables){
            if(closeable != null){
                closeable.close();
            }
        }
    }

    public static void freeAll(NativeResource... resources){
        for(var resource : resources){
            if(resource != null){
                resource.free();
            }
        }
    }

    public static void check(int errCode) throws VulkanException {
        if(errCode != 0){
            throw VulkanException.forError(errCode);
        }
    }

    public static void check(int errCode, String msg) throws VulkanException {
        if(errCode != 0){
            throw VulkanException.forError(errCode, msg);
        }
    }

    public static int firstMatch(boolean[] a, boolean[] b, boolean value){
        for(int i = 0; i < Math.min(a.length, b.length); i++){
            if(a[i] == b[i] == value){
                return i;
            }
        }
        return -1;
    }

    public static boolean contains(int value, int[] options){
        for(var i : options){
            if(value == i){
                return true;
            }
        }
        return false;
    }
}
