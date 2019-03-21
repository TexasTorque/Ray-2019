/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import edu.wpi.first.wpilibj.Timer;

public class HatchIntakeSet extends Command {
  private double startTime;
    private double time;
    private boolean hatchState;

    /**
     * 
     * modes
     * false hatchstate = hold hatch
     * true hatchstate = let go hatch
     */

    public HatchIntakeSet(double delay, boolean hatchState) { 
        super(delay);
        this.hatchState = hatchState;
    } // IntakeSet

    @Override
    protected void init() {
        startTime = Timer.getFPGATimestamp();
        input.setINTuskEngaged(hatchState);
    } // init

	@Override
	protected void continuous() {
	} // continuous

	@Override
	protected boolean endCondition() {
      return (input.getINTuskEngaged() == hatchState);
    } // endCondition

	@Override
	protected void end() {
        input.setINActive(false);
    } // end
}
