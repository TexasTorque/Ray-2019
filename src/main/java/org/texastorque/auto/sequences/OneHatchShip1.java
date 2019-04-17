package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class OneHatchShip1 extends Sequence {

    @Override
    protected void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new RotarySet(0, 1));
        // block1.add(new DriveTime(0.5, 1.5, 0.3));

        ArrayList<Command> block2 = new ArrayList<>();
        Waypoint[] points = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(1, 0, 0),
            new Waypoint(6, 2, 0),
            new Waypoint(12, 1, Pathfinder.d2r(-89))
        };
        block2.add(new DrivePath(0, points, true, false));
        block2.add(new RotarySet(1, 2));
        block2.add(new DriveVisionTime(4, 1.2));
        block2.add(new ClawSet(5.2, false));


        addBlock(block1);
        addBlock(block2);
    }

}