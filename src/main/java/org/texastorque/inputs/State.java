package org.texastorque.inputs;

public class State {
    
    private static volatile State instance;

    public enum RobotState {
        AUTO, TELEOP, VISION, LINE;
    }
    private RobotState robotState = RobotState.TELEOP;

    public RobotState getRobotState() {
        return robotState;
    }

    public void setRobotState(RobotState state) {
        synchronized (this) {
            this.robotState = state;
        }
    }

    public static State getInstance() {
        if (instance == null) {
            synchronized (State.class) {
                if (instance == null)
                    instance = new State();
            }
        }
        return instance;
    }
}