package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import org.texastorque.inputs.State.RobotState;

public class EnableTeleop extends Command {
    
    public EnableTeleop(double delay) {
        super(delay);
    }

    @Override
    protected void init() {}

    @Override
    protected void continuous() {
        input.updateControllers();
    }

    @Override
    protected boolean endCondition() {
        return input.getEndFakeTeleop();
    }

    @Override
    protected void end() {}
}