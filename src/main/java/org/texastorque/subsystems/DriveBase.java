package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class DriveBase extends Subsystem {

    /**
	 *
	 */
	private static volatile DriveBase instance;
    private RobotState currentState;
    private SmartDashboard dashboard;
    private TorqueMotor leftFore;
	private TorqueMotor leftRear;
	private TorqueMotor rightFore;
    private TorqueMotor rightRear;
    private DoubleSolenoid gearShift;

    private double leftSpeed = 0.0;
    private double rightSpeed = 0.0;
    private boolean highGear = false;
    
    private static boolean clockwise = true;
    private boolean angle;
    private int fakeBinary = 0;

    private DriveBase() {
        leftFore = new TorqueMotor(new VictorSP(Ports.DB_LEFT_FORE_MOTOR), !clockwise);
		leftRear = new TorqueMotor(new VictorSP(Ports.DB_LEFT_REAR_MOTOR), !clockwise);
		rightFore = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_FORE_MOTOR), clockwise);
        rightRear = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_REAR_MOTOR), clockwise);
        
        gearShift = new DoubleSolenoid(2, Ports.DB_LEFT_SOLE_A, Ports.DB_LEFT_SOLE_B);

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
            angle = feedback.getAngle();
                
            leftSpeed = 0;
            rightSpeed = 0;

                if (feedback.lineLeftTrue())
                    fakeBinary+= 100;
                // if (feedback.lineMidTrue())
                //     fakeBinary+= 10;
                if (feedback.lineRightTrue())
                    fakeBinary+= 1;
                if (angle)
                    fakeBinary+= 1000;
                // if(leftSpeed < .75){
                //     switch (fakeBinary) {
                //         case 1100: rightSpeed = leftSpeed * 3.5;
                //             break;
                //         case 1001: rightSpeed = leftSpeed * 2.5;
                //             break;
                //         case 0001: leftSpeed = rightSpeed * 3.5;
                //             break;
                //         case 0100: leftSpeed = rightSpeed * 2.5;
                //             break;
                //     }//switch cases
                // }//if left speed <.75
                // else{
                //     switch(fakeBinary){
                //         case 1100: leftSpeed = rightSpeed *.286;
                //             break;
                //         case 1001: leftSpeed = rightSpeed*.4;
                //             break;
                //         case 0001: rightSpeed = leftSpeed*.286;
                //             break;
                //         case 0100: rightSpeed = leftSpeed*.4;
                //             break;
                // }//switch
                // }//else
                
                switch(fakeBinary) {
                    case 1100: rightSpeed += 0.5;
                        break;
                    case 1001: rightSpeed += 0.2;
                        break;
                    case 0001: leftSpeed += 0.5;
                        break;
                    case 0100: leftSpeed += 0.2;
                        break;
                    default: rightSpeed += 0.2;
                        leftSpeed += 0.2;
                        break;
                }//switch
                
                output();
                fakeBinary = 0;
            
        }
        else if (currentState == RobotState.VISION) {
            // Read feedback for NetworkTables input, calculate output
            output();
        }
        
    }

    @Override
    public void output() {
        // setGears();
        
        // if (highGear)
        //     gearShift.set(Value.kForward);
        // else
        //     gearShift.set(Value.kReverse);
        
        leftFore.set(leftSpeed);
		leftRear.set(leftSpeed);
		rightFore.set(rightSpeed);
        rightRear.set(rightSpeed);
        smartDashboard();
    }

    public boolean highGear() {
        return highGear;
    }

    /**
     * Potential auto transmission
     */
    private void setGears() {
        highGear = (leftSpeed > 0.5 && rightSpeed > 0.5) ? true : false;
    }

    @Override
    public void smartDashboard() {
        dashboard.putBoolean("Left", feedback.lineLeftTrue());
        dashboard.putBoolean("Right", feedback.lineRightTrue());
        dashboard.putBoolean("Middle", feedback.lineMidTrue());
        dashboard.putNumber("LeftSpeed", leftSpeed);
        dashboard.putNumber("RightSpeed", rightSpeed);
        dashboard.putBoolean("Tele", (currentState == RobotState.TELEOP));
        dashboard.putBoolean("Line", (currentState == RobotState.LINE));
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