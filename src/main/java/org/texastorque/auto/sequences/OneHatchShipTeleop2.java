package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

// Currently not tuned

public class OneHatchShipTeleop2 extends Sequence {

    @Override
    protected void init() {
        // ArrayList<Command> block1 = new ArrayList<>();
        // Waypoint[] points1 = new Waypoint[] {
        //     new Waypoint(0, 0, 0),
        //     new Waypoint(8, 0, 0)
        // };
        // block1.add(new DrivePath(0, points1, true, false));
        // block1.add(new RotarySet(0, 2));
        // block1.add(new TomInit(0));

        // ArrayList<Command> block2 = new ArrayList<>();
        // block2.add(new EnableTeleop(0));

        // ArrayList<Command> block3 = new ArrayList<>();
        // Waypoint[] points3 = new Waypoint[] {
        //     new Waypoint(0, 0, 0),
        //     new Waypoint(4, 0, 0)
        // };
        // block3.add(new RotarySet(0, 3));
        // block3.add(new DrivePath(0.5, points3, false, false));

        // addBlock(block1);
        // addBlock(block2);
        // addBlock(block3);
    }

}