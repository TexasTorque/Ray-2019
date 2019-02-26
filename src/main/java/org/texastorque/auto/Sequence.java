package org.texastorque.auto;

import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;

public abstract class Sequence {

    private ArrayList<ArrayList<Command>> sequence;
    private double startTime;
    private boolean started;

    protected abstract void init();

    protected Sequence() {
        this.sequence = new ArrayList<ArrayList<Command>>();
        this.started = false;
        init();
    }

    protected void addBlock(ArrayList<Command> block) {
        this.sequence.add(block);
    }

    public void run() {
        if (!started) {
            startTime = Timer.getFPGATimestamp();
            started = true;
            System.out.println(sequence);
        }

        if (sequence.size() > 0) {
            boolean blockEnded = true;
            double currentTime = Timer.getFPGATimestamp();
            for (Command command : sequence.get(0)) {
                if (currentTime - startTime > command.getDelay()) {
                    if (!command.run()) {
                        blockEnded = false;
                    }
                } else {
                    blockEnded = false;
                }
            }

            if (blockEnded) {
                sequence.remove(0);
                startTime = Timer.getFPGATimestamp();
            }
        }
    }
}