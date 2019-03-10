package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class OneHatchRocket3 extends Sequence {

    @Override
    protected void init() {
        // 1
        ArrayList<Command> block1 = new ArrayList<>();
        Waypoint[] points1 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(15, -5, Pathfinder.d2r(30))
        };
        block1.add(new DrivePath(0, points1, false));

        // 2
        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new DriveVision(0));

        // 3
        ArrayList<Command> block3 = new ArrayList<>();
        Waypoint[] points3 = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(30)),
            new Waypoint(3, 1.5, 0)
        };
        block3.add(new DrivePath(0, points3, false));

        // 4
        ArrayList<Command> block4 = new ArrayList<>();
        Waypoint[] points4 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(10, 0, 0)
        };
        block4.add(new DrivePath(0, points4, true));

        addBlock(block1);
        addBlock(block2);
        // addBlock(block3);
        // addBlock(block4);
    }

}