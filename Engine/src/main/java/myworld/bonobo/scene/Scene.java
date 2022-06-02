package myworld.bonobo.scene;

import java.util.IdentityHashMap;

public class Scene {

    protected final String name;
    protected final Node rootNode;

    protected final IdentityHashMap<Spatial, Spatial> sceneContents;

    public Scene(){
        this("");
    }

    public Scene(String name){
        this.name = name;
        rootNode = new Node(name);
        sceneContents = new IdentityHashMap<>();
    }

    public Node getRootNode(){
        return rootNode;
    }
}
