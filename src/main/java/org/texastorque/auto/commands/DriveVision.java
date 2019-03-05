package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

public class DriveVision extends Command {

    private ScheduledPID visionPID;
    private double currentOffset;
    
    public DriveVision(double delay) {
        super(delay);

        visionPID = new ScheduledPID.Builder(0, -0.3, 0.3, 5)
                .setRegions(-0.4, -0.2, 0.2, 0.4)
                .setPGains(0.3, 0.5, 0.8, 0.5, 0.3)
                //.setIGains(0.1, 0, 0, 0, 0.1)
                //.setDGains(0, 0.02, 0, 0.02, 0)
                .build();

        currentOffset = 0;
    }

    @Override
    protected void init() {}

    @Override
    protected void continuous() {
        currentOffset = feedback.getTargetOffset();
        double adjustment = visionPID.calculate(currentOffset);
        System.out.println("Offset: " + currentOffset + " || Adjustment: " + adjustment);

        double leftSpeed = 0.3 - adjustment;
        double rightSpeed = 0.3 + adjustment;

        input.setDBLeftSpeed(leftSpeed);
        input.setDBRightSpeed(rightSpeed);
    }

    @Override
    protected boolean endCondition() {
        return Math.abs(currentOffset) < 0.1 
                && feedback.getULLeft() < 1 
                && feedback.getULRight() < 1;
    }

    @Override
    protected void end() {
        input.setDBLeftSpeed(0);
        input.setDBRightSpeed(0);
    }
}