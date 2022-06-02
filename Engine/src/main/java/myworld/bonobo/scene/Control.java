package myworld.bonobo.scene;

public class Control {

    protected Spatial spatial;

    protected final void setSpatial(Spatial spatial){
        onSpatialSet(spatial);
        this.spatial = spatial;
    }

    protected void onSpatialSet(Spatial newSpatial){}

    public Spatial getSpatial(){
        return spatial;
    }

    public void update(double tpf){}
}
