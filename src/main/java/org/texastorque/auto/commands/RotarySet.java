/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

public class RotarySet extends Command{
    private int setpointIndex = 0;

    public RotarySet(double delay, int index) {
        super(delay);
        this.setpointIndex = index;
    }

    @Override
    protected void init() {
        input.setRTSetpoint(setpointIndex);
    }

    @Override
    protected void continuous() {
    } // continuous

    @Override
    protected boolean endCondition() {
        return Math.abs(feedback.getRTPosition() - input.getRTSetpoint(setpointIndex)) < 2;
    }

    @Override
    protected void end() {}
} // RotarySet
