package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import edu.wpi.first.wpilibj.smartdashboard.*;

import edu.wpi.first.wpilibj.VictorSP;
// import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
// import edu.wpi.first.wpilibj.DoubleSolenoid;

public class DriveBase extends Subsystem {

    private static volatile DriveBase instance;
    private RobotState currentState;
    private SmartDashboard dashboard;
    private TorqueMotor leftFore;
	private TorqueMotor leftRear;
	private TorqueMotor rightFore;
    private TorqueMotor rightRear;
 //   private DoubleSolenoid leftGearShift;
 //   private DoubleSolenoid rightGearShift;
    
    private double leftSpeed = 0.0;
    private double rightSpeed = 0.0;
    private boolean leftHighGear = false;
    private boolean rightHighGear = false;
    
    private static boolean clockwise = true;
    private boolean angle;
    private int fakeBinary = 0;

    private DriveBase() {
        leftFore = new TorqueMotor(new VictorSP(Ports.DB_LEFT_FORE_MOTOR), clockwise);
		leftRear = new TorqueMotor(new VictorSP(Ports.DB_LEFT_REAR_MOTOR), !clockwise);
		rightFore = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_FORE_MOTOR), clockwise);
        rightRear = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_REAR_MOTOR), clockwise);
        
        // leftGearShift = new DoubleSolenoid(2, Ports.DB_LEFT_SOLE_A, Ports.DB_LEFT_SOLE_B);
		// rightGearShift = new DoubleSolenoid(2, Ports.DB_RIGHT_SOLE_A, Ports.IN_RIGHT_SOLE_B);
    }

    @Override
    public void autoInit() {
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void teleopInit() {
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void disabledInit() {
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void disabledContinuous() {
        output();
    }

    @Override
    public void autoContinuous() {
        // Do something
        output();
    }

    @Override
    public void teleopContinuous() {
        currentState = state.getRobotState();

        if (currentState == RobotState.TELEOP) {
            leftSpeed = input.getDBLeftSpeed();
            rightSpeed = input.getDBRightSpeed();
            output();
        }
        else if (currentState == RobotState.LINE) {
            // Read feedback for NetworkTables input, calculate output

            smartDashboard();  
            while (feedback.lineLeftTrue() || feedback.lineRightTrue()) {
                if (feedback.lineLeftTrue())
                    fakeBinary+= 100;
                if (feedback.lineMidTrue())
                    fakeBinary+= 10;
                if (feedback.lineRightTrue())
                    fakeBinary+= 1;
                if (angle)
                    fakeBinary+= 1000;
                switch (fakeBinary) {
                    case 1100: rightSpeed += 0.5;
                    case 1110: rightSpeed += 0.3;
                    case 1001: rightSpeed += 0.2;
                    case 1011: rightSpeed += 0.1;
                    case 0001: leftSpeed += 0.5;
                    case 0011: leftSpeed += 0.3;
                    case 0100: leftSpeed += 0.2;
                    case 0110: leftSpeed += 0.1;
                }
                fakeBinary = 0;
                output();
            }
            
        }
        else if (currentState == RobotState.VISION) {
            // Read feedback for NetworkTables input, calculate output
            output();
        }
        
    }

    @Override
    public void output() {
        setGears();
        /*
        if (leftHighGear)
            leftGearShift.set(Value.kForward);
        else
            leftGearShift.set(Value.kReverse);
        
        if (rightHighGear)
            rightGearShift.set(Value.kForward);
        else
            rightGearShift.set(Value.kReverse);
        */
        leftFore.set(leftSpeed);
		leftRear.set(leftSpeed);
		rightFore.set(rightSpeed);
        rightRear.set(rightSpeed);
    }

    public boolean getLeftHighGear() {
        return leftHighGear;
    }

    public boolean getRightHighGear() {
        return rightHighGear;
    }

    /**
     * Potential auto transmission
     */
    private void setGears() {
        leftHighGear = (leftSpeed < 0.5) ? false : true;
        rightHighGear = (rightSpeed < 0.5) ? false : true;
    }

    @Override
    public void smartDashboard() {
        dashboard.putBoolean("Left", feedback.lineLeftTrue());
        dashboard.putBoolean("Right", feedback.lineRightTrue());
        dashboard.putBoolean("Middle", feedback.lineMidTrue());
    }

  

    public static DriveBase getInstance() {
        if (instance == null) {
            synchronized (DriveBase.class) {
                if (instance == null)
                    instance = new DriveBase();
            }
        }
        return instance;
    }
}