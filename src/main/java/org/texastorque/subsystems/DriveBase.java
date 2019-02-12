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
    private TorqueMotor leftFore;
	private TorqueMotor leftRear;
	private TorqueMotor rightFore;
    private TorqueMotor rightRear;
    private DoubleSolenoid gearShift;
    private SmartDashboard dashboard;

    private double leftSpeed = 0.0;
    private double rightSpeed = 0.0;
    private boolean highGear = false;
    
    private static boolean clockwise = true;
    private boolean angle;
    private double lastAngle = 0.0;
    private String tapeDirection;
    private int fakeBinary = 0;
    private NetworkTable ntLineDetection = NetworkTable.getTable("LineDetection");
    private boolean line = false;

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
        feedback.gyroReset();

        if (currentState == RobotState.TELEOP) {
            line = false;
            leftSpeed = input.getDBLeftSpeed();
            rightSpeed = input.getDBRightSpeed();
            output();
        }
        else if (currentState == RobotState.LINE) {
            
            // Read feedback for NetworkTables input, calculate output
            // angle = feedback.getAngle();
            if(!line)
                tapeDirection = feedback.getTapeDirection();
                line = true;
            
            leftSpeed = 0;
            rightSpeed = 0;

            if (feedback.lineLeftTrue())
                fakeBinary+= 100;

            // if (feedback.lineMidTrue())
            //     fakeBinary+= 10;

            if (feedback.lineRightTrue())
                fakeBinary+= 1;
                
            if (tapeDirection.equals("left"))
                fakeBinary+= 1000;
                
            double angle = Math.abs(ntLineDetection.getDouble("angle", lastAngle));
            lastAngle = angle;
            
            // adjust speed based on initial angle
            double adjustedSpeed = (0.8 * Math.sin(Math.PI * ((90 - angle) / 90))) + 0.2;

            switch(fakeBinary) {
                case 1100: 
                    // rightSpeed += 0.5;
                    rightSpeed = adjustedSpeed;
                    break;
                case 1001: 
                    // rightSpeed += 0.2;
                    rightSpeed = adjustedSpeed;
                    break;
                case 0001: 
                    // leftSpeed += 0.5;
                    leftSpeed = adjustedSpeed;
                    break;
                case 0100: 
                    // leftSpeed += 0.2;
                    leftSpeed = adjustedSpeed;
                    break;
                default: 
                    rightSpeed += 0.2;
                    leftSpeed += 0.2;
                    break;
            }//switch

            // System.out.println(String.valueOf(fakeBinary) + " - L: "  + String.valueOf(leftSpeed) + ", R: " + String.valueOf(rightSpeed));
            // ntLineDetection.putString("rightSpeed", String.valueOf(rightSpeed));
            // ntLineDetection.putString("leftSpeed", String.valueOf(leftSpeed));
            // ntLineDetection.putValue("adjustedSpeed", adjustedSpeed);

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
        
        if (!feedback.closeToWallTrue()){
            leftFore.set(leftSpeed);
            leftRear.set(leftSpeed);
            rightFore.set(rightSpeed);
            rightRear.set(rightSpeed);
        }
        else{
            leftFore.set(-0.2);
            leftRear.set(-0.2);
            rightFore.set(-0.2);
            rightRear.set(-0.2);
        }
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
        dashboard.putBoolean("Tele", (state.getRobotState() == RobotState.TELEOP));
        dashboard.putBoolean("Line", (state.getRobotState() == RobotState.LINE));
        dashboard.putBoolean("Closeness", feedback.closeToWallTrue());
        dashboard.putNumber("Voltage", feedback.getVoltage());
        dashboard.putNumber("Angle", feedback.getRawAngle());
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