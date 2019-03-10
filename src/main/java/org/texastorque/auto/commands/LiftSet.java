package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

public class LiftSet extends Command {

    private int setpointIndex = 0;

    public LiftSet(double delay, int index) {
        super(delay);
        this.setpointIndex = index;
    }

    @Override
    protected void init() {
        input.setLFSetpoint(setpointIndex);
    }

    @Override
    protected void continuous() {}

    @Override
    protected boolean endCondition() {
        return Math.abs(feedback.getLFPosition() - input.getLFSetpoint(setpointIndex)) < 0.3;
    }

    @Override
    protected void end() {}
}