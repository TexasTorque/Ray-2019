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
        block1.add(new RotarySet(0, 2));
        block1.add(new ClawExtenderSet(0.5, false));
        
        ArrayList<Command> block2 = new ArrayList<>();
        Waypoint[] points = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(6, 0, 0)
        };
        block2.add(new DrivePath(0, points, true, false));

        ArrayList<Command> block3 = new ArrayList<>();
        block3.add(new DriveVisionTime(0, 1.5));
        block3.add(new ClawSet(1.5, false));

        ArrayList<Command> block4 = new ArrayList<>();
        points = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(4, 0, 0)
        };
        block4.add(new DrivePath(0.5, points, false, false));

        addBlock(block1);
        addBlock(block2);
        addBlock(block3);
        addBlock(block4);
    }

}