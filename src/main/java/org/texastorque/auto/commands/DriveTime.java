package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import edu.wpi.first.wpilibj.Timer;

public class DriveTime extends Command {

    private double startTime = -1;
    private double time = 0;

    public DriveTime(double time) {
        this.time = time;
        this.done = false;
    }

    @Override
    public boolean run() {
        if (done) {
            return done;
        }

        if (startTime == -1) {
            startTime = Timer.getFPGATimestamp();
        }

        input.setDBLeftSpeed(0.5);
        input.setDBRightSpeed(0.5);

        if (Timer.getFPGATimestamp() - startTime > time) {
            input.setDBLeftSpeed(0);
            input.setDBRightSpeed(0);

            done = true;
        }
        return false;
    }
}