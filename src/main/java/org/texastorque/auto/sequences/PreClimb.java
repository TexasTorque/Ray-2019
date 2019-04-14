package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class PreClimb extends Sequence{

    public void init(){
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new RotarySet(0,4));

        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new LiftSet(0, 7));
        block2.add(new RotarySet(.5, 7));

        ArrayList<Command> block3 = new ArrayList<>();
        block3.add(new TomInit(0));

        ArrayList<Command> block4 = new ArrayList<>();
        block4.add(new LiftSet(0,0));
    }
}