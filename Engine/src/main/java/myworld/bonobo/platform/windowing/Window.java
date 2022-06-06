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

package myworld.bonobo.platform.windowing;

import java.util.concurrent.atomic.AtomicLong;

import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;

public abstract class Window {

    protected final int id;
    protected final long handle;
    protected final AtomicLong surfaceHandle;

    protected Window(int id, long handle){
        this.id = id;
        this.handle = handle;
        surfaceHandle = new AtomicLong(VK_NULL_HANDLE);
    }

    public int getId(){
        return id;
    }

    public void setSurfaceHandle(long surfaceHandle){
        this.surfaceHandle.set(surfaceHandle);
    }

    public boolean hasSurface(){
        return getSurfaceHandle() != VK_NULL_HANDLE;
    }

    public long getSurfaceHandle(){
        return surfaceHandle.get();
    }

    public long getHandle(){
        return handle;
    }

    public abstract void setVisible(boolean visible);

}
