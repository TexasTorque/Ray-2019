package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

public class DriveVision extends Command {

    private ScheduledPID visionPID;
    private double currentOffset;
    
    public DriveVision(double delay) {
        super(delay);

        visionPID = new ScheduledPID.Builder(0, 0.5, 1)
                .setPGains(0.25)
                .build();

        currentOffset = 0;
    }

    @Override
    protected void init() {}

    @Override
    protected void continuous() {
        currentOffset = feedback.getNTTargetOffset();
        double adjustment = visionPID.calculate(currentOffset);

        double baseOutput = 0.5;
        if (feedback.getULLeft() < 1.5 && feedback.getULRight() < 1.5) {
            baseOutput = 0;
        }
        double leftSpeed = baseOutput - adjustment;
        double rightSpeed = baseOutput + adjustment;

        input.setDBLeftSpeed(leftSpeed);
        input.setDBRightSpeed(rightSpeed);
    }

    @Override
    protected boolean endCondition() {
        return Math.abs(currentOffset) < 0.1 
                && feedback.getULLeft() < 1.5 
                && feedback.getULRight() < 1.5;
    }

    @Override
    protected void end() {
        input.setDBLeftSpeed(0);
        input.setDBRightSpeed(0);
    }
}