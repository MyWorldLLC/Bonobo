package myworld.bonobo.demo;

import myworld.bonobo.core.AppSystem;
import myworld.bonobo.time.Timer;

public class HelloSystem extends AppSystem {

    protected Timer timer = Timer.create(1, (elapsed, period, tpf) -> {
        System.out.println("Hello, Bonobo! Tpf is: " + tpf);
    });

    @Override
    public void update(double tpf){
        timer.tick(tpf);
    }
}
