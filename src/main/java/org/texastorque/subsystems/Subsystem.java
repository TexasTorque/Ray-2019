package org.texastorque.subsystems;

import org.texastorque.inputs.*;

import org.texastorque.torquelib.component.TorqueSubsystem;

public abstract class Subsystem implements TorqueSubsystem {

	protected State state = State.getInstance();
    protected Input input = Input.getInstance();
	protected Feedback feedback = Feedback.getInstance();

	protected abstract void output();
}