package org.texastorque.auto;

import org.texastorque.inputs.*;

public abstract class Command {

    protected Input input = Input.getInstance();
    protected Feedback feedback = Feedback.getInstance();
    protected boolean done = false;
    protected double delay = 0; // Seconds

    public Command(double delay) {
        this.delay = delay;
        this.done = false;
    }

    public double getDelay() {
        return delay;
    }

    /**
     * Runs one loop of Command, either to change a setpoint or update the output of a motion profile. Use setters from Input class to do this.
     * 
     * Returns whether or not Command is done.
     */
    public boolean run() {
        if (done) {
            return done;
        }

        // Single-loop operations for when Command is running

        boolean endCondition = false;
        if (endCondition) {
            // Operations for when Command is done
            done = true;
        }
        return false;
    }
}