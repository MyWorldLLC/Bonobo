package myworld.bonobo.time;

public class InlineTimer {

    protected double elapsed;
    protected double period;
    protected final TimedTask task;

    public InlineTimer(double period, TimedTask task){
        elapsed = 0;
        this.period = period;
        this.task = task;
    }

    public static InlineTimer create(double period, TimedTask task){
        return new InlineTimer(period, task);
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

    public void update(double timeStep){
        elapsed += timeStep;
        if(elapsed >= period){
            task.run(elapsed, period, timeStep);
            elapsed = 0;
        }
    }

}
