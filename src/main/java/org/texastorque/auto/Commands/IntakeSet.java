package org.texastorque.auto.commands;
import org.texastorque.auto.Command;
import edu.wpi.first.wpilibj.Timer;

public class IntakeSet extends Command{

    private boolean tuskEngaged;
    private boolean hatchEngaged;
    private double time;
    private double startTime;


    //intake config; true = hatch intake/cargo outtake ,  false = hatch outtake/ cargo intake
    public IntakeSet(double delay, boolean hatch, boolean tusk, double time){
        super(delay);
        this.hatchEngaged = hatch;
        this.time = time;
        this.tuskEngaged = tusk;
        startTime = Timer.getFPGATimestamp();
    }
    protected void init(){
        input.setINHatchEngaged(hatchEngaged);
        input.setINTuskEngaged(tuskEngaged);
        input.setINActive(true); 
    }
    protected void continuous(){}

    protected boolean endCondition(){
        return ((Timer.getFPGATimestamp() - startTime) > time);    
    }

    protected void end(){
        input.setINActive(false);
    }

}