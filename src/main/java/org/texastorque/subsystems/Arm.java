package org.texastorque.subsystems;

public class Arm extends Subsystem {

    public static volatile Arm instance;

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

    public static Arm getInstance() {
        if (instance == null) {
            synchronized (Arm.class) {
                if (instance == null)
                    instance = new Arm();
            }
        }
        return instance;
    }
}