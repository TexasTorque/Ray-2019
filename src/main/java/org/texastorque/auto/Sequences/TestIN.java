package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class TestIN extends Sequence{
    protected void init(){
        ArrayList<Command> blockOne = new ArrayList<>();
        blockOne.add(new DriveTime(0, 1, .25));
        blockOne.add(new IntakeSet(0, true, true, .5));
        blockOne.add(new RotarySet(0, 0));
        blockOne.add(new LiftSet(0, 1));

        ArrayList<Command> blockTwo = new ArrayList<>();
        blockTwo.add(new DriveTime(.5, .5, -.25));
        blockTwo.add(new IntakeSet(.5, false, false, .5));
        blockTwo.add(new RotarySet(.5, 1));
        blockTwo.add(new LiftSet(0, 0));

        addBlock(blockOne);
        //addBlock(blockTwo);

    }

}