package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class TwoHatchRocketFront3 extends Sequence {

    @Override
    protected void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new TomInit(0));
        block1.add(new RotarySet(0, 6));
        block1.add(new ClawSet(1.5, true));

        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new DriveTime(0, 0.3, 0.5));

        


        addBlock(block1);
        addBlock(block2);
    }

}