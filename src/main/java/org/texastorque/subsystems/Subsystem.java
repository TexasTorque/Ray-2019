package org.texastorque.subsystems;

import org.texastorque.inputs.Input;

import org.texastorque.torquelib.component.TorqueSubsystem;

public abstract class Subsystem implements TorqueSubsystem {

    protected Input input;

    public void setInput(Input input) {
		this.input = input;
	}

	public abstract void output();
}