package org.texastorque.subsystems;

import org.texastorque.inputs.*;

import org.texastorque.torquelib.component.TorqueSubsystem;

public abstract class Subsystem implements TorqueSubsystem {

    protected HumanInput humanInput = HumanInput.getInstance();
	protected Feedback feedback = Feedback.getInstance();

	public abstract void output();
}