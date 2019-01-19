package org.texastorque.inputs;

public class Feedback {

    private static Feedback instance;

    private Feedback() {

    }

    public static synchronized Feedback getInstance() {
        return instance == null ? instance = new Feedback() : instance;
    }

}