package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.torquelib.component.TorqueEncoder;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import static org.texastorque.torquelib.util.TorqueMathUtil.near;

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
    private TorqueEncoder leftEncode;
    private TorqueEncoder rightEncode;

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
        leftEncode = new TorqueEncoder(Ports.DB_LEFT_ENCODER_A, Ports.DB_LEFT_ENCODER_B, true, EncodingType.k4X);
        rightEncode = new TorqueEncoder(Ports.DB_RIGHT_ENCODER_A, Ports.DB_RIGHT_ENCODER_B, false, EncodingType.k4X);
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
        feedback.gyroReset();
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
                if (feedback.lineRightTrue())
                    fakeBinary+= 1;
                if (angle)
                    fakeBinary+= 1000;
                switch(fakeBinary) {
                    case 1100: rightSpeed = 0.5;
                        break;
                    case 1001: rightSpeed = 0.2;
                        break;
                    case 0001: leftSpeed = 0.5;
                        break;
                    case 0100: leftSpeed = 0.2;
                        break;
                    default: rightSpeed = 0.2;
                        leftSpeed = 0.2;
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

        if (near(feedback.getRawAngle(), 360.0, 0.3) || near(feedback.getRawAngle(), -360.0, 0.5))
            feedback.gyroReset();

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
     * auto transmission
     */
    private void setGears() {
        //OLD CODE: highGear = (leftSpeed > 0.5 && rightSpeed > 0.5) ? true : false;
        // instead of controller input use encoder
        if (!highGear && ((leftEncode.getAverageRate()+rightEncode.getAverageRate())/2) > 10)
            highGear = true;
        if (highGear && ((leftEncode.getAverageRate()+rightEncode.getAverageRate())/2) > 10)
            highGear = false;
    }
    
    @Override
    public void smartDashboard() {
        dashboard.putBoolean("Left", feedback.lineLeftTrue());
        dashboard.putBoolean("Right", feedback.lineRightTrue());
        dashboard.putBoolean("Middle", feedback.lineMidTrue());
        dashboard.putBoolean("Tele", (state.getRobotState() == RobotState.TELEOP));
        dashboard.putBoolean("Line", (state.getRobotState() == RobotState.LINE));
        dashboard.putBoolean("Closeness", feedback.closeToWallTrue());
        dashboard.putNumber("Voltage", feedback.getDistance());
        dashboard.putNumber("Angle", feedback.getRawAngle());
        dashboard.putNumber("Pitch", feedback.getVertAngle());
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