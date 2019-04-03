package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

public class RotarySet extends Command{
    private int setpointIndex = 0;

    public RotarySet(double delay, int index) {
        super(delay);
        this.setpointIndex = index;
    }

    @Override
    protected void init() {
        input.setRTSetpoint(setpointIndex);
    }

    @Override
    protected void continuous() {
    } 

    @Override
    protected boolean endCondition() {
        return Math.abs(feedback.getRTPosition() - input.calcRTSetpoint(setpointIndex)) < 5.0;
    }

    @Override
    protected void end() {}
}
