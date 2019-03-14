package org.texastorque.inputs;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.torquelib.util.GenericController;
import org.texastorque.torquelib.util.TorqueToggle;

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
    private TorqueToggle LF_encoderState;
    private TorqueToggle RT_encoderState;
    
    private Input() {
        state = State.getInstance();
		driver = new GenericController(0, .1);
        operator = new GenericController(1, .1);

        LF_encoderState = new TorqueToggle();
        RT_encoderState = new TorqueToggle();
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
		DB_leftSpeed = -driver.getLeftYAxis() + 0.6 * driver.getRightXAxis();
        DB_rightSpeed = -driver.getLeftYAxis() - 0.6 * driver.getRightXAxis();

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
    private final double[] LF_setpoints = {0.0, 2.7, 5.0}; // {0.0, 2.5, 5.0};
    private volatile int LF_setpoint = 0;
    private volatile int LF_modifier = 0;
    private volatile double LF_offset = 0;

    private volatile boolean LF_encoderDead = false;
    private volatile double LF_speed = 0.0;

    public void updateLift() {
        LF_encoderState.calc(operator.getRightCenterButton());
        LF_encoderDead = LF_encoderState.get();
        LF_speed = 0;
        
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
            if (LF_encoderDead) {
                LF_speed = -0.3;
            } 
            else {
                LF_offset -= 0.01;
            }
        }
        else if (operator.getRightYAxis() < -0.1) {
            if (LF_encoderDead) {
                LF_speed = 0.3;
            } 
            else {
                LF_offset += 0.01;
            }
        }
    } // updateLift

    public double getLFSetpoint() {
        return LF_setpoints[LF_setpoint] + LF_offset;
    }

    public double getLFSetpoint(int index) {
        return LF_setpoints[index] + LF_offset;
    }

    public double getLFSpeed() {
        return LF_speed;
    }

    public boolean getLFEncoderDead(){
        return LF_encoderDead;
    }

    public void setLFSetpoint(int index) {
        this.LF_setpoint = index;
    }

    // ========== Rotary ==========
    private final double[] RT_setpoints = {0, 45, 71, 88};
    private volatile int RT_setpoint = 0;
    private volatile double RT_offset = 0;
    private boolean elevated = true;

    private volatile boolean RT_encoderDead = false;
    private volatile double RT_speed;
    
    public void updateRotary() {
        RT_encoderState.calc(operator.getLeftCenterButton());
        RT_encoderDead = RT_encoderState.get();
        RT_speed = -.06; // to keep it up against gravity
        if (operator.getDPADDown()) {
            RT_setpoint = 3;
            elevated = false;
        }
        else if (operator.getDPADRight()) {
            RT_setpoint = 2;
            elevated = false;
        }
        else if (operator.getDPADUp()) {
            RT_setpoint = 1;
            elevated = false;
        }
        else if (operator.getDPADLeft()) {
            RT_setpoint = 0;
            elevated = true;
        }
        else if (operator.getLeftYAxis() > 0.1) {
            if (RT_encoderDead) {
                RT_speed = 0.1;
            } 
            else {
                RT_offset += 0.2;
            }
        }
        else if (operator.getLeftYAxis() < -0.1) {
            if (RT_encoderDead) {
                RT_speed = -0.3;
            }
            else {
                RT_offset -= 0.2;
            }
        }
    }

    public double getRTSetpoint() {
        return RT_setpoints[RT_setpoint] + RT_offset;
    }

    public double getRTSetpoint(int i) {
        return RT_setpoints[i] + RT_offset;
    }

    public boolean getElevated(){
        return elevated;
    }

    public double getRTSpeed() {
        return RT_speed;
    }

    public boolean getRTEncoderDead(){
        return RT_encoderDead;
    }

    public void setRTSetpoint(int setpoint){
        RT_setpoint = setpoint;
    }

    // ========== Intake ==========
    private volatile boolean IN_active;
    private volatile boolean IN_hatchState;
    private volatile boolean IN_tuskEngaged = true;
    
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

    public void setINActive(boolean active){
        IN_active = active;
    }

    public void setINHatchState(boolean hatchState){
        IN_hatchState = hatchState;
    }

    public void setINTuskEngaged(boolean tuskEngaged){
        IN_tuskEngaged = tuskEngaged;
    }


    //========== Climber ==========
    private volatile boolean CM_enabled = false;
    private volatile boolean CM_retract = false;
    private volatile boolean lastLeftCenter = false; //being held

    public volatile double CM_tomSpeed;
    public volatile double CM_rearSpeed;
    
    public void updateClimber() {
        CM_retract = false;

        if (driver.getLeftCenterButton() && !lastLeftCenter) {
            CM_enabled = !CM_enabled;
            lastLeftCenter = true;
        } else if (!driver.getLeftCenterButton()) {
            lastLeftCenter = false;
            if (driver.getRightCenterButton()) {
                CM_retract = true;
            }
        }

        // CM_rearSpeed = tester.getLeftYAxis();
        // CM_tomSpeed = tester.getRightYAxis();
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