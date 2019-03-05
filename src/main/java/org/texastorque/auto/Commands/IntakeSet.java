package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

public class IntakeSet extends Command {

    private boolean tuskEngaged;
    private int setPoint;
    private boolean hatchEngaged;

    public IntakeSet(double delay, int setPoint, boolean hatch, boolean tusk){
        super(delay);

        hatchEngaged = hatch;
        tuskEngaged = tusk;
        this.setPoint = setPoint;
    }

    @Override
    protected void init(){
        input.setINTuskEngaged(tuskEngaged);
        input.setHatchState(hatchEngaged);
    }

    @Override
    protected void continuous(){}

    @Override
    protected void end(){
        input.setINActive(false);
    }

    @Override
    protected boolean endCondition(){
        return (Math.abs(feedback.getRTPosition() - setPoint) > 0.2);
    }

}