package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import org.texastorque.torquelib.controlLoop.ScheduledPID;
import org.texastorque.constants.Constants;

import edu.wpi.first.wpilibj.Timer;

public class DriveVisionTime extends Command {

    private ScheduledPID visionPID;
    private double currentOffset;
    private double startTime;
    private double time;
    
    public DriveVisionTime(double delay, double time) {
        super(delay);

        visionPID = new ScheduledPID.Builder(0, 0.5, 1)
                .setPGains(0.5 / Constants.CAMERA_ANGLE_X)
                .build();

        currentOffset = 0;
        this.time = time;
    }

    @Override
    protected void init() {
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    protected void continuous() {
        currentOffset = feedback.getTargetOffset();
        double adjustment = visionPID.calculate(currentOffset);

        double leftSpeed = 0.3 - adjustment;
        double rightSpeed = 0.3 + adjustment;

        input.setDBLeftSpeed(leftSpeed);
        input.setDBRightSpeed(rightSpeed);
    }

    @Override
    protected boolean endCondition() {
        return Timer.getFPGATimestamp() - startTime > time;
    }

    @Override
    protected void end() {
        input.setDBLeftSpeed(0);
        input.setDBRightSpeed(0);
    }
}