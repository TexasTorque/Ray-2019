package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class OneHatchShip2 extends Sequence {

    @Override
    protected void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new TomInit(0));
        block1.add(new RotarySet(0, 5));
        block1.add(new ClawSet(1, true));
        
        ArrayList<Command> block2 = new ArrayList<>();
        Waypoint[] points = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(7, 0, 0)
        };
        block2.add(new TomInit(0));
        block2.add(new DrivePath(0, points, true));
        block2.add(new LiftSet(0, 0));
        block2.add(new RotarySet(1, 3));

        ArrayList<Command> block3 = new ArrayList<>();
        block3.add(new DriveVisionTime(0.5, 2));

        ArrayList<Command> block4 = new ArrayList<>();
        points = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(5, 5, Pathfinder.d2r(120))
        };
        block4.add(new ClawSet(0.5, false));
        block4.add(new RotarySet(1.5, 2));
        block4.add(new DrivePath(1.5, points, false));

        addBlock(block1);
        addBlock(block2);
        addBlock(block3);
        // addBlock(block4);
    }

}