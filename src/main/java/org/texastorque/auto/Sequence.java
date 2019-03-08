package org.texastorque.auto;

import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;

import org.texastorque.inputs.*;

public abstract class Sequence {

    private ArrayList<ArrayList<Command>> sequence; 
    // sequence = arraylist of arraylists - the nested arraylist is a block - within each block is a series of commands

    protected Input input = Input.getInstance();

    private boolean started;
    private double startTime;
    private int blockIndex;

    protected abstract void init();

    protected Sequence() {
        sequence = new ArrayList<ArrayList<Command>>();
        started = false;
        blockIndex = 0;
        init();
    }

    protected void addBlock(ArrayList<Command> block) {
        sequence.add(block);
    }

    public void run() {
        if (!started) {
            startTime = Timer.getFPGATimestamp();
            started = true;
            System.out.println(sequence);
        }

        if (blockIndex < sequence.size()) { // runs all the blocks in each
            boolean blockEnded = true;
            double currentTime = Timer.getFPGATimestamp();
            for (Command command : sequence.get(blockIndex)) {
                if (currentTime - startTime > command.getDelay()) {
                    if (!command.run()) {
                        blockEnded = false;
                    }
                } else {
                    blockEnded = false;
                }
            }

            if (blockEnded) {
                blockIndex++;
                startTime = Timer.getFPGATimestamp();
            }
        }
    }

    public void reset() {
        blockIndex = 0;
        started = false;
    }
} // Sequence