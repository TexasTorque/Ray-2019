package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class DriveBase extends Subsystem {

    private static volatile DriveBase instance;

    private TorqueMotor leftFore;
    private TorqueMotor leftMid;
	private TorqueMotor leftRear;
    private TorqueMotor rightFore;
    private TorqueMotor rightMid;
    private TorqueMotor rightRear;
    private DoubleSolenoid gearShift;
    
    private final ScheduledPID visionPID;
    private double leftSpeed = 0.0;
    private double rightSpeed = 0.0;
    private boolean highGear = false;
    
    private boolean clockwise = true;

    private DriveBase() {
        leftFore = new TorqueMotor(new VictorSP(Ports.DB_LEFT_FORE_MOTOR), !clockwise);
        leftMid = new TorqueMotor(new VictorSP(Ports.DB_LEFT_MID_MOTOR), !clockwise);
        leftRear = new TorqueMotor(new VictorSP(Ports.DB_LEFT_REAR_MOTOR), !clockwise);
        
        rightFore = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_FORE_MOTOR), clockwise);
        rightMid = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_MID_MOTOR), clockwise);
        rightRear = new TorqueMotor(new VictorSP(Ports.DB_RIGHT_REAR_MOTOR), clockwise);
        
        gearShift = new DoubleSolenoid(2, Ports.DB_SOLE_A, Ports.DB_SOLE_B);

        visionPID = new ScheduledPID.Builder(0, -0.5, 0.5, 5)
                .setRegions(-0.4, -0.2, 0.2, 0.4)
                .setPGains(0.3, 0.5, 0.8, 0.5, 0.3)
                //.setIGains(0.1, 0, 0, 0, 0.1)
                //.setDGains(0, 0.02, 0, 0.02, 0)
                .build();
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
    public void run(RobotState state) {
        if (state == RobotState.AUTO) {
        }

        else if (state == RobotState.TELEOP) {
            leftSpeed = input.getDBLeftSpeed();
            rightSpeed = input.getDBRightSpeed();
        }

        else if (state == RobotState.VISION) {
            double currentOffset = feedback.getTargetOffset();
            double adjustment = visionPID.calculate(currentOffset);
            // if (Math.abs(adjustment) < 0.1)
            //     adjustment = 0;
            System.out.println("Offset: " + currentOffset + " || Adjustment: " + adjustment);

            leftSpeed = 0.5 * input.getDBLeftSpeed() - adjustment;
            rightSpeed = 0.5 * input.getDBRightSpeed() + adjustment;

            // leftSpeed = input.getDBLeftSpeed() * (0.5 - adjustment);
            // rightSpeed = input.getDBRightSpeed() * (adjustment + 0.5);
        }

        else if (state == RobotState.LINE) {
            // Read feedback for NetworkTables input, calculate output
        }
        
        setGears(state);
        output();
    }

    @Override
    protected void output() {
        if (highGear)
            gearShift.set(Value.kForward);
        else
            gearShift.set(Value.kReverse);
        
        leftFore.set(leftSpeed);
        leftMid.set(leftSpeed);
        leftRear.set(leftSpeed);
        
		rightFore.set(rightSpeed);
		rightMid.set(rightSpeed);
        rightRear.set(rightSpeed);
    }

    public boolean getHighGear() {
        return highGear;
    }

    /**
     * Potential auto transmission
     */
    private void setGears(RobotState state) {
        if (state == RobotState.TELEOP)
            highGear = input.getDBHighGear();
        else
            highGear = false;
    }

    @Override
    public void disabledContinuous() {}

    @Override
    public void autoContinuous() {}

    @Override
    public void teleopContinuous() {}

    @Override
    public void smartDashboard() {}

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