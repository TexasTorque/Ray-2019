package org.texastorque.auto;

import edu.wpi.first.wpilibj.Timer;

import java.util.Arrays;
import java.util.ArrayList;

public abstract class Sequence {

    private ArrayList<ArrayList<Command>> sequence;
    private double startTime = -1;

    public abstract void init();

    protected void addBlocks(ArrayList<Command>... blocks) {
        sequence.addAll(Arrays.asList(blocks));
    }

    public void run() {
        if (startTime == -1) {
            startTime = Timer.getFPGATimestamp();
        }

        if (sequence.size() > 0) {
            boolean blockEnded = true;
            for (Command command : sequence.get(0)) {
                if (Timer.getFPGATimestamp() - startTime > command.getDelay()) {
                    if (!command.run()) {
                        blockEnded = false;
                    }
                }
                else {
                    blockEnded = false;
                }
            }

            if (blockEnded) {
                sequence.remove(0);
            }
        }
    }
}