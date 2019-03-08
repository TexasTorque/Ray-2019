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

public class Baseline extends Sequence{

    @Override
    protected void init(){
        ArrayList<Command> forewardLift = new ArrayList<>();
        // forewardLift.add(new DrivePath(0,points1));
        forewardLift.add(new DriveTime(0,2,.5));

        input.setLFSetpoint(0);
        forewardLift.add(new LiftSet(2, 2));

        ArrayList<Command> liftRotary = new ArrayList<>();
        liftRotary.add(new RotarySet(0, 1));

        ArrayList<Command> outtake = new ArrayList<>();
        //outtake.add()

        addBlock(forewardLift);
        //addBlock(liftRotary);
        // //addBlock(outtake);
    } // init
} // Baseline
