package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

import edu.wpi.first.wpilibj.Timer;

public class ClawExtenderSet extends Command {

    private boolean extended;

    public ClawExtenderSet(double delay, boolean extended) { 
        super(delay);
        this.extended = extended;
    }

    @Override
    protected void init() {
        input.setINClawExtended(extended);
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
