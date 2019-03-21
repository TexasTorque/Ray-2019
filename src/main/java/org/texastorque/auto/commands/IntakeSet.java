package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

import edu.wpi.first.wpilibj.Timer;

public class IntakeSet extends Command{

    private double startTime;
    private double time;
    private boolean hatchState;

    /**
     * true = hatch intake, cargo outtake
     * false = cargo intake, hatch outtake
     */
    public IntakeSet(double delay, double time, boolean hatchState) { 
        super(delay);
        this.time = time;
        this.hatchState = hatchState;
    }

    @Override
    protected void init() {
        startTime = Timer.getFPGATimestamp();
        input.setINHatchState(hatchState);
        input.setINActive(true);
    }

	@Override
	protected void continuous() {
	}

	@Override
	protected boolean endCondition() {
        return Timer.getFPGATimestamp() - startTime > time;
    }

	@Override
	protected void end() {
        input.setINActive(false);
    }
}
