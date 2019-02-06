package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import edu.wpi.first.wpilibj.VictorSP;

public class Intake extends Subsystem {

    private static volatile Intake instance;
    private RobotState currentState;
    
    private TorqueMotor intakeWheels;
    private DoubleSolenoid intakeWrist;
    private DoubleSolenoid wallIntake;

    private boolean wheelsOn;
    private boolean wristExtended;
    private boolean hatchEngaged;

    private boolean clockwise = true;

    private Intake() {
        intakeWheels = new TorqueMotor(new VictorSP(Ports.IN_MOTOR), clockwise);
        
        intakeWrist = new DoubleSolenoid(2, Ports.IN_WRIST_SOLE_A, Ports.IN_WRIST_SOLE_B);
        wallIntake = new DoubleSolenoid(2, Ports.IN_WALL_SOLE_A, Ports.IN_WALL_SOLE_B);
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
    public void disabledContinuous() {}

    @Override
    public void autoContinuous() {

    }

    @Override
    public void teleopContinuous() {
        currentState = state.getRobotState();

        if (currentState == RobotState.TELEOP) {
            wheelsOn = input.getINWheelsOn();
            wristExtended = input.getINWristExtended();
            hatchEngaged = input.getINHatchEngaged();
        }
        else if (currentState == RobotState.VISION) {
            wheelsOn = false;
            wristExtended = false;
            hatchEngaged = input.getINHatchEngaged();
        }
        else if (currentState == RobotState.LINE) {
            wheelsOn = input.getINWheelsOn();
            wristExtended = input.getINWristExtended();
            hatchEngaged = input.getINHatchEngaged();
        }
        
        output();
    }

    @Override
    public void smartDashboard() {}

    @Override
    public void output() {

    }

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