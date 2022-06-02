package myworld.bonobo.time;

public interface PeriodicTimedTask {

    void run(double elapsed, double period, double tpf);
}
