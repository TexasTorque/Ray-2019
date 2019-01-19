package org.texastorque.subsystems;

import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;

import edu.wpi.first.wpilibj.VictorSP;
// import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
// import edu.wpi.first.wpilibj.DoubleSolenoid;

public class DriveBase extends Subsystem {

    private static DriveBase instance;

    public enum DBMode {
        TELEOP, VISION;
    }
    private DBMode mode;

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
        mode = DBMode.TELEOP;
        leftSpeed = 0.0;
        rightSpeed = 0.0;
    }

    @Override
    public void autoContinuous() {
        //Do something
        output();
    }

    @Override
    public void teleopContinuous() {
        mode = humanInput.getDBMode();

        if (mode == DBMode.TELEOP) {
            leftSpeed = humanInput.getDBLeftSpeed();
            rightSpeed = humanInput.getDBRightSpeed();
            output();
        }
        else if (mode == DBMode.VISION) {
            output();
        }
    }

    private int i = 0;

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

        if (i++ % 100 == 0)
            System.out.println(mode);
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

    public void setMode(DBMode mode) {
        this.mode = mode;
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
    public void smartDashboard() {}

    public static synchronized DriveBase getInstance() {
		return (instance == null) ? instance = new DriveBase() : instance;
	}
}