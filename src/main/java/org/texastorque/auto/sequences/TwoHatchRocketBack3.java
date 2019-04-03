package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class TwoHatchRocketBack3 extends Sequence {

    @Override
    protected void init() {
        ArrayList<Command> block1 = new ArrayList<>();
        block1.add(new TomInit(0));
        block1.add(new RotarySet(0, 5));
        block1.add(new ClawSet(1, true));

        ArrayList<Command> block2 = new ArrayList<>();
        Waypoint[] points = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(18.5, -7, Pathfinder.d2r(-45))
        };
        block2.add(new DrivePath(0, points, false));
        block2.add(new RotarySet(2, 3));

        ArrayList<Command> block3 = new ArrayList<>();
        block3.add(new DriveTurn(0, 35));

        ArrayList<Command> block4 = new ArrayList<>();
        block4.add(new DriveVisionTime(0, 1.2));
        block4.add(new ClawSet(1.2, false));

        ArrayList<Command> block5 = new ArrayList<>();
        points = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(35)),
            new Waypoint(2.5, 0, Pathfinder.d2r(-45))
        };
        block5.add(new DrivePath(0.3, points, false));

        ArrayList<Command> block6 = new ArrayList<>();
        points = new Waypoint[] {
            new Waypoint(0, 0, Pathfinder.d2r(-45)),
            new Waypoint(3, -1.5, Pathfinder.d2r(0)),
            new Waypoint(20, 2, Pathfinder.d2r(0))
        };
        block6.add(new DrivePath(0, points, true));

        ArrayList<Command> block7 = new ArrayList<>();
        block7.add(new DriveVisionTime(0, 1.8));
        block7.add(new ClawSet(1.8, true));


        addBlock(block1);
        addBlock(block2);
        addBlock(block3);
        addBlock(block4);
        addBlock(block5);
        addBlock(block6);
        addBlock(block7);
    }

}