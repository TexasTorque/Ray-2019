package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

public class DriveTurn extends Command {

    private ScheduledPID drivePID;
    private double angle;
    
    public DriveTurn(double delay, double angle) {
        super(delay);
        this.angle = angle;
        drivePID = new ScheduledPID.Builder(angle, 0.5)
                .setPGains(0.8)
                .setDGains(0.1)
                .build();
    }

    @Override
    protected void init() {
        feedback.resetEncoders();
        feedback.resetNavX();
    }

    @Override
    protected void continuous() {
        double speed = 0;

        input.setDBLeftSpeed(speed);
        input.setDBRightSpeed(speed);
    }

    @Override
    protected boolean endCondition() {
        return false;
    }

    @Override
    protected void end() {
        input.setDBLeftSpeed(0);
        input.setDBRightSpeed(0);
    }
}