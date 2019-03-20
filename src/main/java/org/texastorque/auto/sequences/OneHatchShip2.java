package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class OneHatchShip2 extends Sequence {

    @Override
    protected void init() {
        // 1
        ArrayList<Command> block1 = new ArrayList<>();
        Waypoint[] points1 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(9, 0, 0)
        };
        block1.add(new DrivePath(0, points1, true));
        block1.add(new RotarySet(0, 2));
        block1.add(new TomInit(0));

        // 2
        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new DriveVision(0.5));

        // 3
        ArrayList<Command> block3 = new ArrayList<>();
        Waypoint[] points3 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(5, 5, Pathfinder.d2r(120))
        };
        block3.add(new RotarySet(0, 3));
        block3.add(new DrivePath(0.5, points3, false));

        // 4
        ArrayList<Command> block4 = new ArrayList<>();
        Waypoint[] points4 = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(120)),
            new Waypoint(-5, 1, Pathfinder.d2r(180))
        };
        block4.add(new DrivePath(0, points4, true));

        addBlock(block1);
        addBlock(block2);
        // addBlock(block3);
        // addBlock(block4);
    }

}