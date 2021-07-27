package org.texastorque.inputs;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.torquelib.util.GenericController;
import org.texastorque.torquelib.util.TorqueToggle;
import org.texastorque.auto.AutoManager;

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
    // private GenericController tester;

    
    private Input() {
        state = State.getInstance();
		driver = new GenericController(0, .1);
        operator = new GenericController(1, .1);
        // tester = new GenericController(2, .1);
    }
    
    public void updateControllers() {
        updateState();

        if (!driver.getName().equals("")) {
            updateDrive();
            updateIntake();
            updateClimber();
            // updateNetworkTables();
        }

        if (!operator.getName().equals("")) {
            updatePositions();
            updateLift();
            updateRotary();
        }
    }

    public void resetAll() {
        resetDrive();
        resetPositions();
        resetIntake();
        resetClimber();
    }


    // =========== RobotState ==========

    private volatile boolean endFakeTeleop = true;
    private volatile TorqueToggle DBOnly = new TorqueToggle(false);

    public void updateState() {
        DBOnly.calc(operator.getRightBumper());

        if (driver.getXButtonPressed()) {
            if (state.getRobotState() == RobotState.TELEOP) {
                state.setRobotState(RobotState.VISION);
                // NT_pipeline = 0;
            }
            else {
                state.setRobotState(RobotState.TELEOP);
            }
        }
        else if (driver.getBButtonPressed()) {
            if (state.getRobotState() == RobotState.TELEOP) {
                state.setRobotState(RobotState.LINE);
            }
            else if (state.getRobotState() == RobotState.LINE) {
                state.setRobotState(RobotState.TELEOP);
            }
        }
        else if (DBOnly.get()) {
            if (state.getRobotState() == RobotState.TELEOP) {
                state.setRobotState(RobotState.DB_ONLY);
                AutoManager.getInstance().setPreClimb();
            }
            else if (state.getRobotState() == RobotState.DB_ONLY) {
                state.setRobotState(RobotState.TELEOP);
            }
            DBOnly.set(false);
        }

        // endFakeTeleop = false;
        // if (driver.getYButtonPressed()) {
        //     endFakeTeleop = true;
        // }
    }

    public boolean getEndFakeTeleop() {
        return endFakeTeleop;
    }


    // ========== DriveBase ==========

    private volatile double DB_leftSpeed = 0;
    private volatile double DB_rightSpeed = 0;
    private volatile boolean DB_highGear = false;

    public void updateDrive() {
        double rightX = driver.getRightXAxis();
		DB_leftSpeed = -driver.getLeftYAxis() + 0.4 * Math.pow(rightX, 2) * Math.signum(rightX);
        DB_rightSpeed = -driver.getLeftYAxis() - 0.4 * Math.pow(rightX, 2) * Math.signum(rightX);

        if (driver.getRightBumper()) {
            DB_highGear = true;
        }
        else if (driver.getLeftBumper()) {
            DB_highGear = false;
        }
    }

    public void resetDrive() {
        DB_leftSpeed = 0;
        DB_rightSpeed = 0;
        DB_highGear = false;
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

    public void setDBHighGear(boolean gear) {
        DB_highGear = gear;
    }


    // ========== Lift + Rotary ==========

    private volatile int LF_position = 0;
    private volatile int LF_modifier = 0;
    private volatile int RT_position = 0;

    public void updatePositions() {
        if (operator.getAButtonPressed()) {
            LF_position = 0;
        }
        else if (operator.getBButtonPressed()) {
            LF_position = 2;
        }
        else if (operator.getYButtonPressed()) {
            LF_position = 4;
        }
        
        if (operator.getDPADLeft()) {
            LF_modifier = 0;
            RT_position = 0;
        }
        else if (operator.getDPADDown()) {
            LF_modifier = 0;
            RT_position = 3;
        }
        else if (operator.getDPADUp()) {
            LF_modifier = 1;
            RT_position = 1;
        }
        else if (operator.getDPADRight()) {
            LF_modifier = 0;
            RT_position = 2;
        }

        if (operator.getXButtonPressed()) {
            LF_position = 2;
            LF_modifier = 0;
            RT_position = 2;
        }
    }

    public void resetPositions() {
        LF_position = 0;
        LF_modifier = 0;
        RT_position = 0;
    }


    // ========== Lift ==========

    // private final double[] LF_setpoints = {0.0, 1.4, 2.6, 3.7, 5.0, 5.4, 2.2}; 
    private final double[] LF_setpoints = {0.0, 0.6, 2.7, 3.2, 5.0, 5.4, 2.2}; 
    // private volatile int LF_setpoint = 0;
    // private volatile int LF_modifier = 0;
    private volatile double LF_offset = 0;
    private volatile TorqueToggle LF_manualMode = new TorqueToggle(false);
    private volatile double LF_manualOutput = 0;

    public void updateLift() {
        LF_manualMode.calc(operator.getRightCenterButton());

        if (!LF_manualMode.get()) {
            // if (operator.getDPADUp()) {
            //     LF_modifier = 1;
            // }
            // else if (operator.getDPADRight() || operator.getDPADDown() || operator.getDPADLeft()) {
            //     LF_modifier = 0;
            // }

            // if (operator.getAButtonPressed()) {
            //     LF_setpoint = 0;
            // }
            // else if (operator.getBButtonPressed()) {
            //     LF_setpoint = 2;
            // }
            // else if (operator.getYButtonPressed()) {
            //     LF_setpoint = 4;
            // }
            // else if (operator.getXButton()) { //HP station cargo
            //     LF_setpoint = 6;
            //     LF_modifier = 0;
            // }


        

            if (operator.getRightYAxis() > 0.1) {
                // if (LF_offset > -3.0) {
                //     LF_offset -= 0.005;
                // }
                LF_offset -= 0.005;
            }
            else if (operator.getRightYAxis() < -0.1) {
                // if (LF_offset < 3.0) {
                //     LF_offset += 0.005;
                // }
                LF_offset += 0.005;
            }
        }
        else {
            LF_manualOutput = -0.5 * operator.getRightYAxis();
        }
    }

    public double calcLFSetpoint() {
        return LF_setpoints[LF_position + LF_modifier] + LF_offset;
    }

    public double calcLFSetpoint(int index) {
        return LF_setpoints[index] + LF_offset;
    }

    public boolean getLFManualMode() {
        return LF_manualMode.get();
    }

    public double getLFManualOutput() {
        return LF_manualOutput;
    }

    public void setLFPosition(int index) {
        LF_position = index;
        LF_modifier = 0;
    }


    // ========== Rotary ==========

    // private final double[] RT_setpoints = {0, 60, 74, 91, 50, 14};
    private final double[] RT_setpoints = {0, 50, 185, 210, 55};
    // private volatile int RT_setpoint = 0;
    private volatile double RT_offset = 0;
    private volatile TorqueToggle RT_manualMode = new TorqueToggle(false);
    private volatile double RT_manualOutput = 0;

    
    public void updateRotary() {
        RT_manualMode.calc(operator.getLeftCenterButton());
        
        if (!RT_manualMode.get()) {

            // if (operator.getDPADLeft()){
            //     RT_setpoint = 0;
            // }
            // else if (operator.getDPADUp()){
            //     RT_setpoint = 1;
            //     LF_modifier = 1;
            // }
            // else if (operator.getDPADRight()){
            //     RT_setpoint = 2;
            //     LF_modifier = 0;
            // }
            // else if (operator.getDPADDown()){
            //     RT_setpoint = 3;
            // }

            if (operator.getLeftYAxis() > 0.1) {
                // if (RT_offset < 100) {
                //     RT_offset += 0.1;
                // }
                RT_offset += 0.2;
            }
            else if (operator.getLeftYAxis() < -0.1) {
                // if (RT_offset > -100) {
                //     RT_offset -= 0.1;
                // }
                RT_offset -= 0.2;
            }

            // if (LF_setpoint + LF_modifier == 5) {
            //     RT_setpoint = 1;
            // }
        }
        else {
            RT_manualOutput = 0.5 * operator.getLeftYAxis();
        }
    }

    public double calcRTSetpoint() {
        return RT_setpoints[RT_position] + RT_offset;
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

    public void setRTPosition(int index) {
        RT_position = index;
    }


    // ========== Intake ==========

    private volatile boolean IN_active = false;
    private volatile boolean IN_hatchState = false;
    private volatile boolean IN_clawEngaged = true;
    private volatile TorqueToggle IN_extended = new TorqueToggle(false);
    
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
        IN_extended.calc(operator.getLeftBumper());
    }

    public void resetIntake() {
        IN_active = false;
        IN_hatchState = false;
        IN_clawEngaged = true;
        IN_extended = new TorqueToggle(false);
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

    public boolean getINClawExtended() {
        return IN_extended.get();
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

    public void setINClawExtended(boolean extended) {
        IN_extended.set(extended);
    }


    //========== Climber ==========

    private volatile TorqueToggle CM_enabled = new TorqueToggle(false);
    private volatile boolean CM_retract = false;

    private volatile double CM_tomSpeed = 0;
    // private volatile double CM_rearSpeed;
    
    public void updateClimber() {
        // CM_retract = false;
        // CM_enabled.calc(driver.getLeftCenterButton());

        // if (!CM_enabled.get()) {
        //     if (driver.getRightCenterButton()) {
        //         CM_retract = true;
        //     }
        // }

        CM_retract = false;
        if (driver.getRightCenterButton()) {
            CM_retract = true;
            CM_enabled.set(false);
        } 
        else {
            CM_enabled.calc(driver.getLeftCenterButton());
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

    public void resetClimber() {
        CM_enabled = new TorqueToggle(false);
        CM_retract = false;
        CM_tomSpeed = 0;
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


    // ========== NetworkTables ==========
    
    // private int NT_pipeline = 0;

    // public void updateNetworkTables() {
    //     if (driver.getYButtonPressed()) {
    //         NT_pipeline = 1 - NT_pipeline;
    //     }
    //     Feedback.getInstance().getNTPipelineEntry().setNumber(NT_pipeline);
    // }

    
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