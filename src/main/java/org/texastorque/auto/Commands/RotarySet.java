package org.texastorque.auto.commands;
import org.texastorque.auto.Command;

public class RotarySet extends Command{

    private int setPoint;


    //intake config; true = hatch intake/cargo outtake ,  false = hatch outtake/ cargo intake
    public RotarySet(double delay, int RTSetpoint){
        super(delay);
        this.setPoint = RTSetpoint;
    }
    protected void init(){
        input.setRTSetpoint(setPoint);
    }
    protected void continuous(){}

    protected boolean endCondition(){
        return (Math.abs(feedback.getRTPosition() - input.getRTSetpoint()) < .2);    
    }

    protected void end(){}
    
}