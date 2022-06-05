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

package myworld.bonobo.scene;

import java.util.ArrayList;
import java.util.List;

public class Node extends Spatial {

    protected final List<Spatial> children;

    public Node(){
        super();
        children = new ArrayList<>();
    }

    public Node(String name) {
        super(name);
        children = new ArrayList<>();
    }

    public void addChild(Spatial child){
        children.add(child);
        child.setParent(this);
    }

    public boolean isParentOf(Spatial child){
        return child.getParent() == this;
    }

    public void removeChild(Spatial child){
        if(isParentOf(child)){
            children.removeIf(c -> c == child);
            child.setParent(null);
        }
    }

    @Override
    public void update(double tpf){
        super.update(tpf);
        for(Spatial child : children){
            child.update(tpf);
        }
    }

}
