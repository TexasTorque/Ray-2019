/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

import edu.wpi.first.wpilibj.Timer;

public class WheelsIntakeSet extends Command{

    private double startTime;
    private double time;
    private boolean hatchState;

    /**
     * 
     * modes
     * true hatchstate = outtake cargo/intake hatch (floor) = horn hatch outtake
     * false hatchstate = intake cargo/outtake hatch (floor) 
     */

    public WheelsIntakeSet(double delay, double time, boolean hatchState) { 
        super(delay);
        this.time = time;
        this.hatchState = hatchState;
    } // IntakeSet

    @Override
    protected void init() {
        startTime = Timer.getFPGATimestamp();
        input.setINActive(true);
        input.setINHatchState(hatchState);
    } // init

	@Override
	protected void continuous() {
	} // continuous

	@Override
	protected boolean endCondition() {
        return Timer.getFPGATimestamp() - startTime > time;
    } // endCondition

	@Override
	protected void end() {
        input.setINActive(false);
    } // end
        
} // IntakeSet
