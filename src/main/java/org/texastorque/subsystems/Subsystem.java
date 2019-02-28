package org.texastorque.subsystems;

import org.texastorque.inputs.*;
import org.texastorque.inputs.State.RobotState;

import org.texastorque.torquelib.component.TorqueSubsystem;

public abstract class Subsystem implements TorqueSubsystem {

	protected State state = State.getInstance();
    protected Input input = Input.getInstance();
	protected Feedback feedback = Feedback.getInstance();

	public abstract void run(RobotState state);
	protected abstract void output();
}