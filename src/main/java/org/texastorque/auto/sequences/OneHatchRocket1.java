package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class OneHatchRocket1 extends Sequence {

    @Override
    protected void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        Waypoint[] points1 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(11, -8, Pathfinder.d2r(-30))
        };
        block1.add(new DrivePath(0, points1, true));
        // block1.add(new LiftSet(1, 1));

        ArrayList<Command> block2 = new ArrayList<>();
        Waypoint[] points2 = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(-60)),
            new Waypoint(4, 4, Pathfinder.d2r(60))
        };
        block2.add(new DrivePath(0, points2, false));
        // block2.add(new LiftSet(1, 0));

        addBlock(block1);
        addBlock(block2);
    }

}