package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

import edu.wpi.first.wpilibj.Timer;

public class ClawSet extends Command {

    // true = holding hatch, false = open
    private boolean clawEngaged;

    public ClawSet(double delay, boolean engaged) { 
        super(delay);
        this.clawEngaged = engaged;
    }

    @Override
    protected void init() {
        input.setINClawEngaged(clawEngaged);
    }

	@Override
	protected void continuous() {}

	@Override
	protected boolean endCondition() {
        return true;
    }

	@Override
	protected void end() {}
}
