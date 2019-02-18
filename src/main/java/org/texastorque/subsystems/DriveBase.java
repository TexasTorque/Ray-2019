package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.torquelib.component.TorqueEncoder;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.networktables.*;

import org.texastorque.torquelib.controlLoop.ScheduledPID;
import org.texastorque.torquelib.controlLoop.ScheduledPID.*;
import static org.texastorque.torquelib.util.TorqueMathUtil.near;

public class DriveBase extends Subsystem {

    /**
	 *
	 */
	private static volatile DriveBase instance;
    private RobotState currentState;
    private TorqueMotor leftFore;
    private TorqueMotor leftMid;
	private TorqueMotor leftRear;
    private TorqueMotor rightFore;
    private TorqueMotor rightMid;
    private TorqueMotor rightRear;
    private DoubleSolenoid gearShift;
    private SmartDashboard dashboard;

    private double leftSpeed = 0.0;
    private double rightSpeed = 0.0;
    private boolean highGear = false;
    
    private static boolean clockwise = true;

    private ScheduledPID linePID;
    private boolean reset;
    private double angleDegree = 45;
    private String lineDirection;

    private DriveBase() {
        leftFore = new TorqueMotor(new VictorSP(Ports.DB_LEFT_FORE_MOTOR), !clockwise);
        leftMid = new TorqueMotor(new VictorSP(Ports.DB_LEFT_MID_MOTOR), !clockwise);
		leftRear = new TorqueMotor(new VictorSP(Ports.DB_LEFT_REAR_MOTOR), !clockwise);
        rightFore = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_FORE_MOTOR), clockwise);
        rightMid = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_MID_MOTOR), clockwise);
        rightRear = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_REAR_MOTOR), clockwise);
        
        gearShift = new DoubleSolenoid(2, Ports.DB_GEAR_SOLE_A, Ports.DB_GEAR_SOLE_B);
       
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
            reset = true;
            leftSpeed = input.getDBLeftSpeed();
            rightSpeed = input.getDBRightSpeed();
            output();
        }
        else if (currentState == RobotState.LINE) {
            // Read feedback for NetworkTables input, calculate output
            output();
            
        }
        else if (currentState == RobotState.VISION) {
            // Read feedback for NetworkTables input, calculate output
            output();
        }
        
    }

    @Override
    protected void output() {

        // if (near(feedback.getRawAngle(), 360.0, 0.3) || near(feedback.getRawAngle(), -360.0, 0.5))
        //     feedback.gyroReset();

        // setGears();
        
        // if (highGear)
        //     gearShift.set(Value.kForward);
        // else
        //     gearShift.set(Value.kReverse);
            leftFore.set(leftSpeed);
            leftMid.set(leftSpeed);
            leftRear.set(leftSpeed);
            rightFore.set(rightSpeed);
            rightMid.set(rightSpeed);
            rightRear.set(rightSpeed);
        

        smartDashboard();
        
    }

    public boolean highGear() {
        return highGear;
    }

    /**
     * auto transmission
     */
    // private void setGears() {
    //     //OLD CODE: highGear = (leftSpeed > 0.5 && rightSpeed > 0.5) ? true : false;
    //     // instead of controller input use encoder
    //     if (!highGear && ((leftEncode.getAverageRate()+rightEncode.getAverageRate())/2) > 10)
    //         highGear = true;
    //     if (highGear && ((leftEncode.getAverageRate()+rightEncode.getAverageRate())/2) > 10)
    //         highGear = false;
    // }
    
    @Override
    public void smartDashboard() {
        dashboard.putBoolean("Tele", (state.getRobotState() == RobotState.TELEOP));
        dashboard.putBoolean("Line", (state.getRobotState() == RobotState.LINE));
        dashboard.putNumber("LeftSpeed", leftSpeed);
        dashboard.putNumber("RightSpeed", rightSpeed);
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