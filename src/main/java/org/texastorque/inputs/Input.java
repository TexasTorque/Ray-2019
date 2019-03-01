package org.texastorque.inputs;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.torquelib.util.GenericController;
import org.texastorque.torquelib.component.TorqueEncoder;
import org.texastorque.inputs.Feedback.*;

/**
 * All forms of input, including driver/operator controllers and input from the code itself.
 * 
 * Setters should only be used by Commands. Subsystems should only use getters.
 */

/**
 * All forms of input, including driver/operator controllers and input from the code itself.
 * 
 * Setters should only be used by Commands. Subsystems should only use getters.
 */
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
    
    public void updateControllers() {
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

    public void setDBLeftSpeed(double speed) {
        this.DB_leftSpeed = speed;
    }

    public void setDBRightSpeed(double speed) {
        this.DB_rightSpeed = speed;
    }

    public boolean getDBHighGear() {
        return DB_highGear;
    }


    // ========== Lift ==========
    private final double[] LF_setpoints = {0.0, 2.5, 5.0};
    private double LF_offset = 0;
    private volatile int LF_setpoint;

    public void updateLift() {
        if (operator.getAButtonPressed()) {
            LF_setpoint = 0;
        }
        else if (operator.getBButtonPressed()) {
            LF_setpoint = 1;
        }
        else if (operator.getYButtonPressed()) {
            LF_setpoint = 2;
        }
        else if (operator.getRightYAxis() > 0.1) {
            LF_offset -= 0.005;
        }
        else if (operator.getRightYAxis() < -0.1) {
            LF_offset += 0.005;
        }
    }

    public double getLFSetpoint() {
        return LF_setpoints[LF_setpoint] + LF_offset;
    }

    public double getLFSetpoint(int index) {
        return LF_setpoints[index] + LF_offset;
    }

    public void setLFSetpoint(int index) {
        this.LF_setpoint = index;
    }

    // ========== Rotary ==========
    private final double[] RT_setpoints = {0, 60, 85};
    private volatile int RT_setpoint;
    private volatile double RT_offset = 0;
    
    public void updateRotary() {
        if (operator.getDPADDown()) {
            RT_setpoint = 2;
        }
        else if (operator.getDPADRight()) {
            RT_setpoint = 1;
        }
        else if (operator.getDPADUp()) {
            RT_setpoint = 0;
        }
        else if (operator.getLeftYAxis() > 0.1) {
            RT_offset += 0.2;
        }
        else if (operator.getLeftYAxis() < -0.1) {
            RT_offset -= 0.2;
        }
    }

    public double getRTSetpoint() {
        return RT_setpoints[RT_setpoint] + RT_offset;
    }

    public double getRTSetpoint(int i) {
        return RT_setpoints[i] + RT_offset;
    }

    // ========== Intake ==========
    private volatile boolean IN_active;
    private volatile boolean IN_hatchState;
    private volatile boolean IN_tuskEngaged = true;
    
    // right bumper = cargo intake/hatch outtake
    public void updateIntake() {
        IN_active = false;

        if (driver.getLeftTrigger()) { 
            IN_active = true;
            IN_hatchState = true;
        } // hatch intake, cargo outtake
        else if (driver.getRightTrigger()) {
            IN_active = true;
            IN_hatchState = false;
        } //hatch outtake, cargo intake
        
        if (driver.getAButtonPressed()) {
            IN_tuskEngaged = false;
        } 
        else if (driver.getAButtonReleased()) {
            IN_tuskEngaged = true;
        }
    }

    public boolean getINActive() {
        return IN_active;
    }

    public boolean getHatchState() {
        return IN_hatchState;
    }

    public boolean getINTuskEngaged() {
        return IN_tuskEngaged;
    }


    //========== Climber ==========
    private volatile boolean CM_enabled;
    private volatile boolean CM_retract;
    private boolean lastLeftCenter = false;
    
    public void updateClimber() {
        CM_retract = false;

        if (driver.getLeftCenterButton() && !lastLeftCenter) {
            CM_enabled = !CM_enabled;
            lastLeftCenter = true;
        } else if (!driver.getLeftCenterButton()) {
            lastLeftCenter = false;
        }

        else if (driver.getRightCenterButton()) {
            CM_retract = true;
        }
    }

    public boolean getCMEnabled() {
        return CM_enabled;
    }

    public boolean getCMRetract() {
        return CM_retract;
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