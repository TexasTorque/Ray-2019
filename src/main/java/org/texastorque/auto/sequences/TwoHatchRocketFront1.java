package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class TwoHatchRocketFront1 extends Sequence {

    @Override
    protected void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new RotarySet(0, 1));
        // block1.add(new DriveTime(0.5, 1.5, 0.3));

        ArrayList<Command> block2 = new ArrayList<>();
        Waypoint[] points = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(8, 7.5, Pathfinder.d2r(35))
        };
        block2.add(new DrivePath(0, points, true, false));
        block2.add(new RotarySet(1, 2));
        block2.add(new DriveVisionTime(1.8, 1.2));
        block2.add(new ClawSet(3, false));

        ArrayList<Command> block3 = new ArrayList<>();
        // block3.add(new DriveVisionTime(0, 1.2));
        // block3.add(new ClawSet(0, false));

        ArrayList<Command> block4 = new ArrayList<>();
        points = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(0)),
            new Waypoint(8, 3, Pathfinder.d2r(60))
        };
        block4.add(new DrivePath(0, points, false, false));

        ArrayList<Command> block5 = new ArrayList<>();
        points = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(0)),
            new Waypoint(5, 5, Pathfinder.d2r(85))
        };
        block5.add(new DrivePath(0, points, true, false));
        block5.add(new DriveVisionTime(1.8, 1.2));
        block5.add(new ClawSet(3.0, true));

        ArrayList<Command> block6 = new ArrayList<>();
        // block6.add(new DriveVisionTime(0, 1.2));
        // block6.add(new ClawSet(0, true));

        ArrayList<Command> block7 = new ArrayList<>();
        points = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(0)),
            new Waypoint(8, -4.5, Pathfinder.d2r(-85))
        };
        block7.add(new DrivePath(0, points, false, false));

        ArrayList<Command> block8 = new ArrayList<>();
        points = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(0)),
            new Waypoint(4, -3, Pathfinder.d2r(-60))
        };
        block8.add(new DrivePath(0, points, true, false));
        block8.add(new LiftSet(0.5, 4));
        block8.add(new DriveVisionTime(1, 1.2));
        // block8.add(new ClawSet(2.2, false));

        ArrayList<Command> block9 = new ArrayList<>();
        // block9.add(new DriveVisionTime(0, 1.2));
        // block9.add(new ClawSet(1.2, false));


        addBlock(block1);
        addBlock(block2);
        // addBlock(block3);
        addBlock(block4);
        addBlock(block5);
        // addBlock(block6);
        addBlock(block7);
        addBlock(block8);
        // addBlock(block9);
    }

}