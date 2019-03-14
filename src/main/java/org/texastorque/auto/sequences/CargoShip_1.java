/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.texastorque.auto.sequences;

import org.texastorque.auto.Sequence;
import org.texastorque.auto.Command;
import org.texastorque.auto.commands.*;

import jaci.pathfinder.*;
import java.util.ArrayList;

public class CargoShip_1 extends Sequence{
    @Override 
    protected void init() {
        // 1 - go to cargoship
        ArrayList<Command> block1 = new ArrayList<>();
        Waypoint[] points1 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(4, 2, Pathfinder.d2r(0)) // switch to -2 for the other hatch
        };
        block1.add(new DrivePath(0, points1, true));
        block1.add(new RotarySet(0, 3));

        // 2 - place cargo
        ArrayList<Command> block2 = new ArrayList<>();
        block2.add(new RotarySet(0,4));

        // 3 - back to hab again
        ArrayList<Command> block3 = new ArrayList<>();
        Waypoint[] points3 = new Waypoint[] {
            new Waypoint(0,0,0),
            new Waypoint(4,0,0)
        };
        block3.add(new DrivePath(0, points3, false));
        block3.add(new RotarySet(0,3));

        addBlock(block1);
        addBlock(block2);
        addBlock(block3);
    } // init
} // CargoShip_1 - goes to left hatch
