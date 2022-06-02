package myworld.bonobo.scene;

import myworld.bonobo.math.TransformBuffer;

import java.util.ArrayList;
import java.util.List;

public class Spatial {
    protected final String name;
    protected final TransformBuffer transform;
    protected List<Control> controls;
    protected Spatial parent;

    public Spatial(){
        this("");
    }

    public Spatial(String name){
        this.name = name;
        transform = new TransformBuffer();
    }

    public String getName(){
        return name;
    }

    public Spatial getParent(){
        return parent;
    }

    protected void setParent(Spatial parent){
        this.parent = parent;
    }

    public boolean isChild(){
        return parent != null;
    }

    public void addControl(Control control){
        if(controls == null){
            controls = new ArrayList<>();
        }
        control.setSpatial(this);
        controls.add(control);
    }

    public void removeControl(Control control){
        if(control.getSpatial() != this){
            return;
        }
        controls.removeIf(c -> c == control);
        control.setSpatial(null);
        if(controls.isEmpty()){
            controls = null;
        }
    }

    public void update(double tpf){
        if(transform.needsGpuTransformRefresh()){
            transform.buildGpuTransform();
        }

        if(controls != null){
            for(Control control : controls){
                control.update(tpf);
            }
        }
    }

    // TODO - get/set local/world location, rotation, & scale

}
