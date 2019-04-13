package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import org.texastorque.torquelib.controlLoop.ScheduledPID;
import jaci.pathfinder.Pathfinder;

public class DriveTurn extends Command {

    private ScheduledPID turnPID;
    private double targetAngle;

    private double currentYaw;
    private double speed;

    public DriveTurn(double delay, double targetAngle) {
        super(delay);
        this.targetAngle = Pathfinder.boundHalfDegrees(targetAngle);

        turnPID = new ScheduledPID.Builder(targetAngle, 0.5)
            .setPGains(0.023)
            .build();
    }

    @Override
    protected void init() {
        feedback.resetDriveEncoders();
        feedback.zeroYaw();
        input.setDBHighGear(false);
    }

    @Override
    protected void continuous() {
        currentYaw = -feedback.getYaw(); // navX yaw is (+) going CW
        // double reverseYaw = currentYaw - Math.signum(currentYaw) * 360;
        
        // double currentError = Math.abs(targetAngle - currentYaw);
        // double reverseError = Math.abs(targetAngle - reverseYaw);
        // double effectiveYaw = currentError < reverseError ? currentYaw : reverseYaw;

        // speed = turnPID.calculate(effectiveYaw);
        speed = turnPID.calculate(currentYaw);

        input.setDBLeftSpeed(-speed);
        input.setDBRightSpeed(speed);
    }

    @Override
    protected boolean endCondition() {
        return Math.abs(targetAngle - currentYaw) < 3.0 && Math.abs(feedback.getDBLeftSpeed()) < 0.5;
    }

    @Override
    protected void end() {
        input.setDBLeftSpeed(0);
        input.setDBRightSpeed(0);
    }
}