package org.texastorque.auto.commands;

import org.texastorque.auto.Command;
import edu.wpi.first.wpilibj.Timer;

public class TomInit extends Command {

    private double startTime;
    private double time = 0.23;
    
    public TomInit(double delay) {
        super(delay);
    }

    @Override
    protected void init() {
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    protected void continuous() {
        input.setCMTomSpeed(0.5);
    }

    @Override
    protected boolean endCondition() {
        return Timer.getFPGATimestamp() - startTime > time;
    }

    @Override
    protected void end() {
        input.setCMTomSpeed(0);
    }
}