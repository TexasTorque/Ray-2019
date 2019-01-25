package org.texastorque.inputs;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.torquelib.util.GenericController;

public class Input {

    private static volatile Input instance;

    private State state;
	private GenericController driver;
    private  GenericController operator;
    
    private Input() {
        state = State.getInstance();
		driver = new GenericController(0, .1);
		operator = new GenericController(1, .1);
    }
    
    public void update() {
        updateState();
        updateDrive();
    }

    // =========== RobotState ==========
    public void updateState() {
        if (driver.getXButtonPressed()) {
            if (state.getRobotState() == RobotState.TELEOP) {
                state.setRobotState(RobotState.LINE);
            }
            else {
                state.setRobotState(RobotState.TELEOP);
            }
        }
    }
    
    // ========== DriveBase ==========
    private double DB_leftSpeed = 0;
    private double DB_rightSpeed = 0;

    public void updateDrive() {
		DB_leftSpeed = -driver.getLeftYAxis() + driver.getRightXAxis();
        DB_rightSpeed = -driver.getLeftYAxis() - driver.getRightXAxis();
    }

    public double getDBLeftSpeed() {
        return DB_leftSpeed;
    }

    public double getDBRightSpeed() {
        return DB_rightSpeed;
    }

    // ========== Lift ==========
    private final double[] LF_setpoints = {0, 0, 0};
    private volatile int LF_setpoint;

    public void updateLift() {}

    public double getLFSetpoint() {
        return LF_setpoints[LF_setpoint];
    }

    //  ========== Arm ==========
    private final double[] AM_setpoints = {0, 0, 0};
    private volatile int AM_setpoint;

    public void updateArm() {}

    public double getAMSetpoint() {
        return AM_setpoints[AM_setpoint];
    }
    
    public static Input getInstance() {
        if (instance == null) {
            synchronized (Input.class) {
                if (instance == null)
                    instance = new Input();
            }
        }
        return instance;
    }
    
}