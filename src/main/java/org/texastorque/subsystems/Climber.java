package org.texastorque.subsystems;

import org.texastorque.constants.Ports;
import org.texastorque.inputs.State.RobotState;

public class Climber extends Subsystem {

    public static volatile Climber instance;

    @Override
    public void autoInit() {

    }

    @Override
    public void teleopInit() {

    }

    @Override
    public void disabledInit() {

    }

    @Override
    public void disabledContinuous() {

    }

    @Override
    public void autoContinuous() {

    }

    @Override
    public void teleopContinuous() {

    }

    @Override
    public void smartDashboard() {

    }

    @Override
    public void output() {

    }

    public static Climber getInstance() {
        if (instance == null) {
            synchronized (Climber.class) {
                if (instance == null)
                    instance = new Climber();
            }
        }
        return instance;
    }
}