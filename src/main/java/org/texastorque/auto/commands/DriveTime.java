package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import edu.wpi.first.wpilibj.Timer;

public class DriveTime extends Command {

    private double startTime;
    private double time;
    private double speed;
    
    public DriveTime(double delay, double time, double speed) {
        super(delay);
        this.time = time;
        this.speed = speed;
    }

    @Override
    protected void init() {
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    protected void continuous() {
        input.setDBLeftSpeed(speed);
        input.setDBRightSpeed(speed);
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