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
        
        //forewardLift.add(new DriveTime(0,2,.5));

        forewardLift.add(new RotarySet(1,1));

        // input.setLFSetpoint(0);
        // forewardLift.add(new LiftSet(2, 2));

        addBlock(forewardLift);
    } // init
} // Baseline
