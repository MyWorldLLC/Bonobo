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

package myworld.bonobo.util;

import java.util.ArrayList;
import java.util.List;

public class ResourceScope implements AutoCloseable {

    protected final List<AutoCloseable> closeables;

    public ResourceScope(){
        closeables = new ArrayList<>();
    }

    public <T extends AutoCloseable> T add(T freeable){
        closeables.add(freeable);
        return freeable;
    }

    @Override
    public void close() throws Exception {
        for(AutoCloseable resource : closeables){
            resource.close();
        }
        closeables.clear();
    }
}
