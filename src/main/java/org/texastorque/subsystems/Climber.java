package org.texastorque.subsystems;

public class Climber extends Subsystem {

    private static volatile Climber instance;
    

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