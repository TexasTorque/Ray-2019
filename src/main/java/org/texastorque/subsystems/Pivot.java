package org.texastorque.subsystems;

public class Pivot extends Subsystem {

    public static volatile Pivot instance;

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

    public static Pivot getInstance() {
        if (instance == null) {
            synchronized (Pivot.class) {
                if (instance == null)
                    instance = new Pivot();
            }
        }
        return instance;
    }
}