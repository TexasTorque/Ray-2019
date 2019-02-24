package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

import edu.wpi.first.wpilibj.Encoder;
import jaci.pathfinder.*;
import jaci.pathfinder.modifiers.TankModifier;
import jaci.pathfinder.followers.EncoderFollower;

public class DrivePath extends Command {

    private EncoderFollower leftFollower;
    private EncoderFollower rightFollower;

    /**
     * Go to https://www.chiefdelphi.com/t/pathfinder-coordinate-system/159870 to see how Waypoint coordinates work
     */
    public DrivePath(double delay, Waypoint[] points) {
        super(delay);

        /**
         * Fit method: HERMITE_CUBIC or HERMITE_QUINTIC
         * Sample count: SAMPLES_HIGH (100000), SAMPLES_LOW (10000), SAMPLES_FAST (1000)
         * Time step (s)
         * Max velocity (ft/s)
         * Max Acceleration (ft/s/s)
         * Max Jerk (ft/s/s/s)
         */
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_LOW, 0.01, 12.0, 8.0, 100.0);
        
        Trajectory path = Pathfinder.generate(points, config);
        TankModifier modifier = new TankModifier(path);
        modifier.modify(2); // DriveBase width (ft)

        leftFollower = new EncoderFollower(modifier.getLeftTrajectory());
        rightFollower = new EncoderFollower(modifier.getRightTrajectory());
        leftFollower.configurePIDVA(0.5, 0, 0, 1/12.0, 0);
        rightFollower.configurePIDVA(0.5, 0, 0, 1/12.0, 0);
    }

    @Override
    public void init() {
        /**
         * Initial position
         * Pulses per rotation
         * Wheel diameter (ft)
         */
        leftFollower.configureEncoder(feedback.getDBLeftRaw(), 1000, 0.5);
        rightFollower.configureEncoder(feedback.getDBRightRaw(), 1000, 0.5);
    }

	@Override
	protected void continuous() {
		input.setDBLeftSpeed(leftFollower.calculate(feedback.getDBLeftRaw()));
        input.setDBRightSpeed(rightFollower.calculate(feedback.getDBRightRaw()));
	}

	@Override
	protected boolean endCondition() {
		return leftFollower.isFinished() && rightFollower.isFinished();
	}

	@Override
	protected void end() {
		input.setDBLeftSpeed(0);
        input.setDBRightSpeed(0);
	}
}