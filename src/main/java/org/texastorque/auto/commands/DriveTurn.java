package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import org.texastorque.torquelib.controlLoop.ScheduledPID;
import jaci.pathfinder.Pathfinder;

public class DriveTurn extends Command {

    private ScheduledPID turnPID;
    private double targetAngle;
    private boolean clockwise;

    private double currentYaw;
    private double speed;
    
    public DriveTurn(double delay, double angle) {
        super(delay);
        angle = Pathfinder.boundHalfDegrees(angle);
        currentYaw = -feedback.getYaw();
        clockwise = Math.abs(angle - currentYaw) < 180;
        targetAngle = clockwise ? angle : reflectAngle(angle);

        turnPID = new ScheduledPID.Builder(targetAngle, 0.6)
                .setPGains(0.015)
                .build();
    }

    private double reflectAngle(double angle) {
        if (angle < 0) {
            return -180 - angle;
        }
        else {
            return 180 - angle;
        }
    }

    @Override
    protected void init() {
        feedback.resetDriveEncoders();
    }

    @Override
    protected void continuous() {
        currentYaw = -feedback.getYaw();

        if (clockwise) {
            speed = turnPID.calculate(currentYaw);

            input.setDBLeftSpeed(-speed);
            input.setDBRightSpeed(speed);
        }
        else {
            currentYaw = reflectAngle(currentYaw);
            speed = turnPID.calculate(currentYaw);

            input.setDBLeftSpeed(speed);
            input.setDBRightSpeed(-speed);
        }
    }

    @Override
    protected boolean endCondition() {
        return Math.abs(targetAngle - currentYaw) < 3.0 && Math.abs(speed) < 0.1;
    }

    @Override
    protected void end() {
        input.setDBLeftSpeed(0);
        input.setDBRightSpeed(0);
    }
}