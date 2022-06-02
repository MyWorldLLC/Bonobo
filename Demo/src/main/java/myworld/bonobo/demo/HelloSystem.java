package myworld.bonobo.demo;

import myworld.bonobo.core.AppSystem;

public class HelloSystem extends AppSystem {

    @Override
    public void update(double tpf){
        System.out.println("Hello, Bonobo! Tpf is: " + tpf);
    }
}
