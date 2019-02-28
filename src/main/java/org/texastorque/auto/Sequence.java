package org.texastorque.auto;

import java.util.ArrayList;

public abstract class Sequence {

    protected ArrayList<ArrayList<Command>> sequence = new ArrayList<>();

    /**
     * Add command blocks
     */
    public abstract void init();

    public void run() {
        if (sequence.size() == 0) {

        } else {
            boolean blockDone = true;
            for (Command command : sequence.get(0)) {
                if (!command.run()) {
                    blockDone = false;
                }
            }

            if (blockDone) {
                sequence.remove(0);
            }
        }
    }
}