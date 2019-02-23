package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import edu.wpi.first.wpilibj.VictorSP;

public class Intake extends Subsystem {

    private static volatile Intake instance;
    
    private TorqueMotor intakeWheels;
    private DoubleSolenoid intakeWristLeft;
    private DoubleSolenoid intakeWristRight;
    private double intakeSpeed;

    private boolean wheelsOn;
    private boolean wristExtended;
    private boolean hatchEngaged;

    private boolean clockwise = true;

    private Intake() {
        intakeWheels = new TorqueMotor(new VictorSP(Ports.IN_MOTOR), clockwise);
        
        intakeWristLeft = new DoubleSolenoid(2, Ports.IN_HATCH_LEFT_SOLE_A, Ports.IN_HATCH_LEFT_SOLE_B);
        intakeWristRight = new DoubleSolenoid(2, Ports.IN_HATCH_RIGHT_SOLE_A, Ports.IN_HATcH_RIGHT_SOLE_B);
    }

    @Override
    public void autoInit() {
        wheelsOn = false;
        wristExtended = false;
        hatchEngaged = false;
    }

    @Override
    public void teleopInit() {
        wheelsOn = false;
    }

    @Override
    public void disabledInit() {
        wheelsOn = false;
    }

    @Override
    public void run(RobotState state) {
        if (state == RobotState.AUTO) {
        }

        else if (state == RobotState.TELEOP) {
            wheelsOn = input.getINWheelsOn();
            wristExtended = input.getINWristExtended();
            hatchEngaged = input.getINHatchEngaged();
            if (wheelsOn)
                intakeSpeed = .5;    
        }

        else if (state == RobotState.VISION) {
            wheelsOn = false;
            wristExtended = false;
            hatchEngaged = input.getINHatchEngaged();
        }

        else if (state == RobotState.LINE) {
            wheelsOn = input.getINWheelsOn();
            wristExtended = input.getINWristExtended();
            hatchEngaged = input.getINHatchEngaged();
        }
        
        output();
    }

    @Override
    public void output() {
        if (wristExtended){
            intakeWristLeft.set(Value.kForward);
            intakeWristRight.set(Value.kForward);
        }
        else {
            intakeWristLeft.set(Value.kReverse);
            intakeWristRight.set(Value.kReverse);
        }

    }

    @Override
    public void disabledContinuous() {}

    @Override
    public void autoContinuous() {}

    @Override
    public void teleopContinuous() {}

    @Override
    public void smartDashboard() {}

    public static Intake getInstance() {
        if (instance == null) {
            synchronized (Intake.class) {
                if (instance == null)
                    instance = new Intake();
            }
        }
        return instance;
    }
}