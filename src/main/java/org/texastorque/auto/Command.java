package org.texastorque.auto;

import org.texastorque.inputs.*;

public abstract class Command {

    protected Input input = Input.getInstance();
    protected Feedback feedback = Feedback.getInstance();
    private double delay; // Seconds
    private boolean started;
    private boolean ended;

    protected Command(double delay) {
        this.delay = delay;
        started = false;
        ended = false;
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
        if (ended) {
            return ended;
        }

        // Operations on first loop
        if (!started) {
            init();
            started = true;
        }

        // Single-loop operations for when Command is running
        continuous();

        // Operations for when Command is done
        if (endCondition()) {
            end();
            ended = true;
        }
        return false;
    }

    protected abstract void init();

    protected abstract void continuous();

    protected abstract boolean endCondition();

    protected abstract void end();
}