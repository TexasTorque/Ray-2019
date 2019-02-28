package org.texastorque.auto;

import org.texastorque.inputs.*;

public abstract class Command {

    protected Input input = Input.getInstance();
    protected Feedback feedback = Feedback.getInstance();
    protected boolean done = false;

    public abstract boolean run();
}