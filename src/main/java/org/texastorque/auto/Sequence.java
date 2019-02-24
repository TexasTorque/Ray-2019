package org.texastorque.auto;

import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;

public abstract class Sequence {

    protected ArrayList<ArrayList<Command>> sequence = new ArrayList<>();
    private double startTime = -1;

    /**
     * Add command blocks
     */
    public abstract void init();

    public void run() {
        if (startTime == -1) {
            startTime = Timer.getFPGATimestamp();
        }

        if (sequence.size() > 0) {
            boolean blockDone = true;
            for (Command command : sequence.get(0)) {
                if (Timer.getFPGATimestamp() - startTime > command.getDelay()) {
                    if (!command.run()) {
                        blockDone = false;
                    }
                }
                else {
                    blockDone = false;
                }
            }

            if (blockDone) {
                sequence.remove(0);
            }
        }
    }
}