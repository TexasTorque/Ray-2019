package org.texastorque.inputs;

public class Feedback {

    private static volatile Feedback instance;

    private Feedback() {

    }

    // Read encoders

    // Read RPi feedback from NetworkTables

    // Read line-following sensors

    public static Feedback getInstance() {
        if (instance == null) {
            synchronized (Feedback.class) {
                if (instance == null)
                    instance = new Feedback();
            }
        }
        return instance;
    }

}