package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

import edu.wpi.first.wpilibj.Timer;

public class FangSet extends Command{

    // true = holding hatch, false = open
    private boolean fangEngaged;

    public FangSet(double delay, boolean fangEngaged) { 
        super(delay);
        this.fangEngaged = fangEngaged;
    }

    @Override
    protected void init() {
        input.setINFangEngaged(fangEngaged);
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
