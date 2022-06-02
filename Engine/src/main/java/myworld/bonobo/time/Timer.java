package myworld.bonobo.time;

public class Timer {

    protected double elapsed;
    protected double period;
    protected final PeriodicTimedTask task;

    public Timer(double period, PeriodicTimedTask task){
        elapsed = 0;
        this.period = period;
        this.task = task;
    }

    public static Timer create(double period, PeriodicTimedTask task){
        return new Timer(period, task);
    }

    public void setPeriod(double period){
        this.period = period;
    }

    public double getPeriod(){
        return period;
    }

    public double getElapsed(){
        return elapsed;
    }

    public void tick(double timeStep){
        elapsed += timeStep;
        if(elapsed >= period){
            task.run(elapsed, period, timeStep);
            elapsed = 0;
        }
    }

}
