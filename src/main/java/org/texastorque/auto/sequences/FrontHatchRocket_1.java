package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class FrontHatchRocket_1 extends Sequence{
    @Override
    protected void init() {
        
        // 1 - drive to front of rocket
        ArrayList<Command> block1 = new ArrayList<>();
        Waypoint[] points1 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(7, 5, Pathfinder.d2r(60)),
        };
        block1.add(new DrivePath(0, points1, true));
        
        // 2 - outtake
        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new LiftSet(0,0));
        block2.add(new RotarySet(0,1));
        block2.add(new HatchIntakeSet(0,true));

        addBlock(block1);
        addBlock(block2);
    } // init
} // FrontHatchRocket_1
