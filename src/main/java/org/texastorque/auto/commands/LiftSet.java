package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import edu.wpi.first.wpilibj.Timer;

public class LiftSet extends Command {

    private int setpointIndex = 0;

    public LiftSet(int index) {
        this.setpointIndex = index;
        this.done = false;
    }

    @Override
    public boolean run() {
        if (done) {
            return done;
        }

        input.setLFSetpoint(setpointIndex);

        if (Math.abs(feedback.getLFPosition() - input.getLFSetpoint(setpointIndex)) < 0.2) {
            done = true;
        }
        return false;
    }
}