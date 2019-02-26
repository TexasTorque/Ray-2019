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
    private DoubleSolenoid hatchWrist;

    private double wheelsSpeed;//+= forward - = backwards
    private boolean hatchEngaged;//true = parallel false = perpendicular


    private boolean clockwise = true;

    private Intake() {
        intakeWheels = new TorqueMotor(new VictorSP(Ports.IN_MOTOR), clockwise);
        
        hatchWrist = new DoubleSolenoid(0, Ports.IN_HATCH_SOLE_A, Ports.IN_HATCH_SOLE_B);
    }

    @Override
    public void autoInit() {
        hatchEngaged = false;
        hatchWrist.set(Value.kReverse);
        wheelsSpeed = 0;
    }

    @Override
    public void teleopInit() {
        wheelsSpeed = 0;
    }

    @Override
    public void disabledInit() {
        wheelsSpeed = 0;
        hatchEngaged = false;
        output();
    }

    @Override
    public void run(RobotState state) {
        if (state == RobotState.AUTO) {
        }

        else if (state == RobotState.TELEOP) {
            wheelsSpeed = input.getINWheelsSpeed();
            hatchEngaged = input.getINHatchEngaged();
        }

        else if (state == RobotState.VISION) {
            wheelsSpeed = 0;
            hatchEngaged = input.getINHatchEngaged();
        }

        else if (state == RobotState.LINE) {
            hatchEngaged = input.getINHatchEngaged();
        }
        
        output();
    }

    @Override
    public void output() {
        if(hatchEngaged)
            hatchWrist.set(Value.kReverse);
        else    
            hatchWrist.set(Value.kForward);
        intakeWheels.set(wheelsSpeed);
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