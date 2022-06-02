package myworld.bonobo.time;

public interface TimedTask {

    void run(double elapsed, double period, double tpf);
}
