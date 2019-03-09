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

public class TestStuff extends Sequence{

    @Override
    protected void init(){

        ArrayList<Command> block1 = new ArrayList<>();
        Waypoint[] points1 = new Waypoint[] {
            new Waypoint(0, 0, 0),
            new Waypoint(4, -4, Pathfinder.d2r(-30))
        }; // go to rocket first hatch
        block1.add(new DrivePath(0, points1, true));
        block1.add(new RotarySet(0, 1));
        //block1.add(new IntakeSet(0.5, .5, false)); // brings hatch down from horizontal to vertical by spinning the wheels

        //ArrayList<Command> block2 = new ArrayList<>();
        //block2.add(new IntakeSet(1, 1, true)); // outtake hatch

        // ArrayList<Command> block3 = new ArrayList<>();
        // block3.add(new RotarySet(0,0)); // bring rotary down into center of hatch

        // ArrayList<Command> block4 = new ArrayList<>();
        // Waypoint[] points4 = new Waypoint[] {
        //     new Waypoint(0,0, Pathfinder.d2r(45)),
        //     new Waypoint(4,-4,Pathfinder.d2r(0))
        // } ; // come back to starting position
        // block3.add(new DrivePath(0, points4, false));

        // ArrayList<Command> block5 = new ArrayList<>();
        // Waypoint[] points5 = new Waypoint[] {
        //     new Waypoint (0,0,Pathfinder.d2r(0)),
        //     new Waypoint(1,0,Pathfinder.d2r(0))
        // }; // go to cargo ship
        // block4.add(new DrivePath(0, points5, true));

        // ArrayList<Command> block6 = new ArrayList<>();
        // block6.add(new IntakeSet(1, 2, true)); // outtake hatch

        // SOMEWHERE IN THE MIDDLE MAKE IT GO TO THE HUMAN PLAYER STATION

        addBlock(block1);
        //addBlock(block2);
        // addBlock(block3);
        // addBlock(block4);
        // addBlock(block5);
        // addBlock(block6);
        
    } // init

} // TestStuff
