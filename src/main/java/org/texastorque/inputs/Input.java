package org.texastorque.inputs;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.torquelib.util.GenericController;

public class Input {

    private static volatile Input instance;

    private volatile State state;
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
        updateLift();
        updateRotary();
        updateIntake();
        updateClimber();
    }

    // =========== RobotState ==========
    public void updateState() {
        if (driver.getXButtonPressed()) {
            if (state.getRobotState() == RobotState.TELEOP) {
                state.setRobotState(RobotState.VISION);
            }
            else {
                state.setRobotState(RobotState.TELEOP);
            }
        }
    }
    
    // ========== DriveBase ==========
    private volatile double DB_leftSpeed = 0;
    private volatile double DB_rightSpeed = 0;
    private volatile boolean DB_highGear = false;

    public void updateDrive() {
		DB_leftSpeed = -driver.getLeftYAxis() + driver.getRightXAxis();
        DB_rightSpeed = -driver.getLeftYAxis() - driver.getRightXAxis();

        if (driver.getRightBumper()) {
            DB_highGear = true;
        }
        else if (driver.getLeftBumper()) {
            DB_highGear = false;
        }
    }

    public double getDBLeftSpeed() {
        return DB_leftSpeed;
    }

    public double getDBRightSpeed() {
        return DB_rightSpeed;
    }

    public boolean getDBHighGear() {
        return DB_highGear;
    }


    // ========== Lift ==========
    private final double[] LF_setpoints = {0, 10, 20};
    private volatile int LF_setpoint;

    public void updateLift() {}

    public double getLFSetpoint() {
        return LF_setpoints[LF_setpoint];
    }

    public double getLFSetpoint(int i) {
        return LF_setpoints[i];
    }

    // ========== Rotary ==========
    private final double[] RT_setpoints = {0, 10};
    private volatile int RT_setpoint;

    public void updateRotary() {}

    public double getRTSetpoint() {
        return RT_setpoints[RT_setpoint];
    }

    public double getRTSetpoint(int i) {
        return RT_setpoints[i];
    }

    // ========== Intake ==========
    private volatile boolean IN_wheelsOn;
    private volatile boolean IN_wristExtended;
    private volatile boolean IN_hatchEngaged;

    public void updateIntake() {}

    public boolean getINWheelsOn() {
        return IN_wheelsOn;
    }

    public boolean getINWristExtended() {
        return IN_wristExtended;
    }

    public boolean getINHatchEngaged() {
        return IN_hatchEngaged;
    }


    //Climber
    private volatile boolean CM_enabled;
    
    public void updateClimber() {
        if (driver.getAButtonPressed()) {
            CM_enabled = !CM_enabled;
        }
    }

    public boolean getCMEnabled() {
        return CM_enabled;
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