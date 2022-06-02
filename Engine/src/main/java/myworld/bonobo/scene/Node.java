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
