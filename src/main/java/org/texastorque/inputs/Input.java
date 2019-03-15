package org.texastorque.inputs;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.torquelib.util.GenericController;
import org.texastorque.torquelib.util.TorqueToggle;

/**
 * All forms of input, including driver/operator controllers and input from the code itself.
 * 
 * Setters should only be used by Commands. Subsystems should only use getters.
 */
public class Input {

    private static volatile Input instance;

    private volatile State state;
	private GenericController driver;
    private GenericController operator;
    private GenericController tester;
    
    private Input() {
        state = State.getInstance();
		driver = new GenericController(0, .1);
        operator = new GenericController(1, .1);
        tester = new GenericController(2, .1);
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

    private final double[] LF_setpoints = {0.0, 1.5, 2.5, 4.0, 4.5, 5.0}; // {0.0, 2.5, 5.0};
    private volatile int LF_setpoint = 0;
    private volatile int LF_modifier = 0;
    private volatile double LF_offset = 0;
    private volatile TorqueToggle LF_manualMode = new TorqueToggle(false);
    private volatile double LF_manualOutput = 0;

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
                LF_setpoint = 0 + LF_modifier;
            }
            else if (operator.getBButtonPressed()) {
                LF_setpoint = 2 + LF_modifier;
            }
            else if (operator.getYButtonPressed()) {
                LF_setpoint = 4 + LF_modifier;
            }
            else if (operator.getRightYAxis() > 0.1) {
                LF_offset -= 0.005;
            }
            else if (operator.getRightYAxis() < -0.1) {
                LF_offset += 0.005;
            }
        }
        else {
            LF_manualOutput = -0.5 * operator.getRightYAxis();
        }
    }

    public double getLFSetpoint() {
        return LF_setpoints[LF_setpoint] + LF_offset;
    }

    public double getLFSetpoint(int index) {
        return LF_setpoints[index] + LF_offset;
    }

    public boolean getLFManualMode() {
        return LF_manualMode.get();
    }

    public double getLFManualOutput() {
        return LF_manualOutput;
    }

    public void setLFSetpoint(int index) {
        LF_setpoint = index;
    }


    // ========== Rotary ==========

    private final double[] RT_setpoints = {0, 45, 80, 95};
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
            else if (operator.getLeftYAxis() > 0.1) {
                RT_offset += 0.2;
            }
            else if (operator.getLeftYAxis() < -0.1) {
                RT_offset -= 0.2;
            }
        }
        else {
            RT_manualOutput = 0.5 * operator.getLeftYAxis();
        }
    }

    public double getRTSetpoint() {
        return RT_setpoints[RT_setpoint] + RT_offset;
    }

    public double getRTSetpoint(int index) {
        return RT_setpoints[index] + RT_offset;
    }

    public boolean getRTManualMode() {
        return RT_manualMode.get();
    }

    public double getRTManualOutput() {
        return RT_manualOutput;
    }

    public void setRTSetpoint(int index) {
        RT_setpoint = index;
    }


    // ========== Intake ==========

    private volatile boolean IN_active = false;
    private volatile boolean IN_hatchState = false;
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
        } // hatch outtake, cargo intake
        
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