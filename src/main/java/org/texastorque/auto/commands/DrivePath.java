package org.texastorque.auto.commands;

import org.texastorque.auto.Command;

import edu.wpi.first.wpilibj.Encoder;
import jaci.pathfinder.*;
import jaci.pathfinder.modifiers.TankModifier;
import jaci.pathfinder.followers.EncoderFollower;

public class DrivePath extends Command {

    private EncoderFollower leftFollower;
    private EncoderFollower rightFollower;

    public DrivePath(double delay, Waypoint[] points) {
        super(delay);

        /**
         * Fit method: HERMITE_CUBIC or HERMITE_QUINTIC
         * Sample count: SAMPLES_HIGH (100000), SAMPLES_LOW (10000), SAMPLES_FAST (1000)
         * Time step (s)
         * Max velocity (m/s)
         * Max Acceleration (m/s/s)
         * Max Jerk (m/s/s/s)
         * 
         * 1 m = 3.281 ft
         */
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_LOW, 0.01, 3.0, 2.0, 60.0);
        
        Trajectory path = Pathfinder.generate(points, config);
        TankModifier modifier = new TankModifier(path);
        modifier.modify(0.6); // DriveBase width (m)

        leftFollower = new EncoderFollower(modifier.getLeftTrajectory());
        rightFollower = new EncoderFollower(modifier.getRightTrajectory());
        leftFollower.configureEncoder(feedback.getDBLeftRaw(), 1000, 0.1524);
        rightFollower.configureEncoder(feedback.getDBRightRaw(), 1000, 0.1524);
        leftFollower.configurePIDVA(0.5, 0, 0, 1/3.0, 0);
        rightFollower.configurePIDVA(0.5, 0, 0, 1/3.0, 0);
    }

    @Override
    public boolean run() {
        if (done) {
            return done;
        }

        double leftSpeed = leftFollower.calculate(feedback.getDBLeftRaw());
        double rightSpeed = rightFollower.calculate(feedback.getDBRightRaw());

        input.setDBLeftSpeed(leftSpeed);
        input.setDBRightSpeed(rightSpeed);

        if (leftFollower.isFinished() && rightFollower.isFinished()) {
            input.setDBLeftSpeed(0);
            input.setDBRightSpeed(0);

            done = true;
        }
        return false;
    }
}