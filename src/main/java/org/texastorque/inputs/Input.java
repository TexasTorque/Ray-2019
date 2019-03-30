package org.texastorque.inputs;

// temp stuff
// import java.awt.event.KeyEvent;
// import java.awt.event.KeyListener;
// import javax.swing.JFrame;
// import javax.swing.JTextField;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.torquelib.util.GenericController;
import org.texastorque.torquelib.util.TorqueToggle;

/**
 * All forms of input, including driver/operator controllers and input from the code itself.
 * 
 * Setters should only be used by Commands. Subsystems should only use getters.
 */

public class Input{

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

    private volatile boolean endFakeTeleop = false;

    public void updateState() {
        if (driver.getXButtonPressed()) {
            if (state.getRobotState() == RobotState.TELEOP) {
                state.setRobotState(RobotState.VISION);
            }
            else {
                state.setRobotState(RobotState.TELEOP);
            }
        }
        else if (driver.getBButtonPressed()) {
            if (state.getRobotState() == RobotState.TELEOP) {
                state.setRobotState(RobotState.LINE);
            }
            else {
                state.setRobotState(RobotState.TELEOP);
            }
        }

        endFakeTeleop = false;
        if (driver.getYButtonPressed()) {
            endFakeTeleop = true;
        }
    }

    public boolean getEndFakeTeleop() {
        return endFakeTeleop;
    }


    // ========== DriveBase ==========

    private volatile double DB_leftSpeed = 0;
    private volatile double DB_rightSpeed = 0;
    private volatile boolean DB_highGear = false;

    public void updateDrive() {
		DB_leftSpeed = -driver.getLeftYAxis() + 0.4 * driver.getRightXAxis();
        DB_rightSpeed = -driver.getLeftYAxis() - 0.4 * driver.getRightXAxis();

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

    private final double[] LF_setpoints = {0.0, 1.0, 2.6, 3.5, 5.0, 5.5, 2.2}; 
    private volatile int LF_setpoint = 0;
    private volatile int LF_modifier = 0;
    private volatile double LF_offset = 0;
    private volatile TorqueToggle LF_manualMode = new TorqueToggle(false);
    private volatile double LF_manualOutput = 0;

    private volatile boolean LF_encoderDead = false;
    private volatile double LF_speed = 0.0;

    public void updateLift() {
        LF_manualMode.calc(operator.getRightCenterButton());

        if (!LF_manualMode.get()) {
            if (operator.getDPADUp()) {
                LF_modifier = 1;
            }
            else if (operator.getDPADRight() || operator.getDPADDown() || operator.getDPADLeft()) {
                LF_modifier = 0;
            }

            if (operator.getAButtonPressed()) {
                LF_setpoint = 0;
            }
            else if (operator.getBButtonPressed()) {
                LF_setpoint = 2;
            }
            else if (operator.getYButtonPressed()) {
                LF_setpoint = 4;
            }
            else if (operator.getXButton()) { //HP station cargo
                LF_setpoint = 6;
                LF_modifier = 0;
            }
            else if (operator.getRightYAxis() > 0.1) {
                if (LF_offset > -1) {
                    LF_offset -= 0.005;
                }
            }
            else if (operator.getRightYAxis() < -0.1) {
                if (LF_offset < 1) {
                    LF_offset += 0.005;
                }
            }
        }
        else {
            LF_manualOutput = -0.5 * operator.getRightYAxis();
        }
    } // updateLift

    public double calcLFSetpoint() {
        return LF_setpoints[LF_setpoint + LF_modifier] + LF_offset;
    }

    public double calcLFSetpoint(int index) {
        return LF_setpoints[index] + LF_offset;
    }

    public double getLFSpeed() {
        return LF_speed;
    }

    public boolean getLFEncoderDead(){
        return LF_encoderDead;
    }
    
    public boolean getLFManualMode() {
        return LF_manualMode.get();
    }

    public double getLFManualOutput() {
        return LF_manualOutput;
    }

    public void setLFSetpoint(int index) {
        LF_modifier = index % 2;
        LF_setpoint = index - LF_modifier;
    }


    // ========== Rotary ==========

    private final double[] RT_setpoints = {0, 43, 74, 95, 50};
    private volatile int RT_setpoint = 0;
    private volatile double RT_offset = 0;
    private volatile TorqueToggle RT_manualMode = new TorqueToggle(false);
    private volatile double RT_manualOutput = 0;
    
    public void updateRotary() {
        RT_manualMode.calc(operator.getLeftCenterButton());
        
        if (!RT_manualMode.get()) {
            if (operator.getDPADDown()) {
                RT_setpoint = 3;
            }
            else if (operator.getDPADRight()) {
                RT_setpoint = 2;
            }
            else if (operator.getDPADUp()) {
                RT_setpoint = 1;
            }
            else if (operator.getDPADLeft()) {
                RT_setpoint = 0;
            }
            else if (operator.getXButton()) { // HP station cargo
                RT_setpoint = 4;
            }
            else if (operator.getLeftYAxis() > 0.1) {
                if (RT_offset < 20) {
                    RT_offset += 0.1;
                }
            }
            else if (operator.getLeftYAxis() < -0.1) {
                if (RT_offset > -20) {
                    RT_offset -= 0.1;
                }
            }
        }
        else {
            RT_manualOutput = 0.5 * operator.getLeftYAxis();
        }
    }

    public double calcRTSetpoint() {
        return RT_setpoints[RT_setpoint] + RT_offset;
    }

    public double calcRTSetpoint(int index) {
        return RT_setpoints[index] + RT_offset;
    }

    public boolean getRTManualMode() {
        return RT_manualMode.get();
    }

    public double getRTManualOutput() {
        return RT_manualOutput;
    }

    public int getRTSetpoint() {
        return RT_setpoint;
    }

    public double getRTSetpoint(int setpoint) {
        return RT_setpoints[setpoint];
    }

    public void setRTSetpoint(int index) {
        RT_setpoint = index;
    }

    public boolean getElevated() {
        if (RT_setpoint < 2) {
            return true;
        } 
        else {
            return false;
        }
    } // return elevation of rotary


    // ========== Intake ==========

    private volatile boolean IN_active = false;
    private volatile boolean IN_hatchState = false;
    private volatile boolean IN_clawEngaged = true;
    
    public void updateIntake() {
        IN_active = false;

        if (driver.getLeftTrigger()) { 
            IN_active = true;
            IN_hatchState = true;
        } // hatch intake, cargo outtake
        else if (driver.getRightTrigger()) {
            IN_active = true;
            IN_hatchState = false;
        } // hatch outtake, cargo intake
        
        if (driver.getAButtonPressed()) {
            IN_clawEngaged = !IN_clawEngaged;
        } 
    }

    public boolean getINActive() {
        return IN_active;
    }

    public boolean getHatchState() {
        return IN_hatchState;
    }

    public boolean getINClawEngaged() {
        return IN_clawEngaged;
    }

    public void setINClawEngaged(boolean engaged) {
        IN_clawEngaged = engaged;
    }

    public void setINActive(boolean active) {
        IN_active = active;
    }

    /**
     * true = hatch intake, cargo outtake
     * false = cargo intake, hatch outtake
     */
    public void setINHatchState(boolean state) {
        IN_hatchState = state;
    }


    //========== Climber ==========

    private volatile TorqueToggle CM_enabled = new TorqueToggle(false);
    private volatile boolean CM_retract = false;

    private volatile double CM_tomSpeed;
    private volatile double CM_rearSpeed;
    
    public void updateClimber() {
        CM_retract = false;
        CM_enabled.calc(driver.getLeftCenterButton());

        if (!CM_enabled.get()) {
            if (driver.getRightCenterButton()) {
                CM_retract = true;
            }
        }

        CM_tomSpeed = 0;
        if (driver.getDPADDown()) {
            CM_tomSpeed = 0.4;
        }
        else if (driver.getDPADUp()) {
            CM_tomSpeed = -0.4;
        }

        // CM_rearSpeed = tester.getLeftYAxis();
        // CM_tomSpeed = tester.getRightYAxis();
    }

    public boolean getCMEnabled() {
        return CM_enabled.get();
    }

    public boolean getCMRetract() {
        return CM_retract;
    }

    public double getCMTomSpeed() {
        return CM_tomSpeed;
    }

    public void setCMTomSpeed(double speed) {
        CM_tomSpeed = speed;
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