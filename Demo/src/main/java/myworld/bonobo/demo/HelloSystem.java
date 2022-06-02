package myworld.bonobo.demo;

import myworld.bonobo.core.BaseSystem;

public class HelloSystem extends BaseSystem {

    @Override
    public void update(double tpf){
        System.out.println("Hello, Bonobo! Tpf is: " + tpf);
    }
}
