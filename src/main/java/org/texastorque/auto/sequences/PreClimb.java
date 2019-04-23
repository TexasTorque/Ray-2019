package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import java.util.ArrayList;

public class PreClimb extends Sequence {

    public void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new RotarySet(0, 1));
        block1.add(new LiftSet(0, 6));

        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new TomInit(0, 0.28));

        ArrayList<Command> block3 = new ArrayList<>();
        block3.add(new LiftSet(0, 0));
        block3.add(new RotarySet(0.3, 0));
        

        addBlock(block1);
        addBlock(block2);
        addBlock(block3);
    }
}