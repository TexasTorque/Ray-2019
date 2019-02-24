package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import edu.wpi.first.wpilibj.VictorSP;

public class FourBarIntake extends Subsystem {

    private static volatile FourBarIntake instance;
    
    private TorqueMotor intakeWheels;
    private DoubleSolenoid hatchLeft;
    private DoubleSolenoid hatchRight;

    private boolean wheelsOn;
    private boolean hatchEngaged;

    private boolean clockwise = true;

    private FourBarIntake() {
        intakeWheels = new TorqueMotor(new VictorSP(Ports.IN_MOTOR), clockwise);

        hatchLeft = new DoubleSolenoid(2, Ports.IN_HATCH_LEFT_SOLE_A, Ports.IN_HATCH_LEFT_SOLE_B);
        hatchRight = new DoubleSolenoid(2, Ports.IN_HATCH_RIGHT_SOLE_A, Ports.IN_HATCH_RIGHT_SOLE_B);
    }

    @Override
    public void autoInit() {
        wheelsOn = false;
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
            hatchEngaged = input.getINHatchEngaged();
        }

        else if (state == RobotState.VISION) {
            wheelsOn = false;
            hatchEngaged = input.getINHatchEngaged();
        }

        else if (state == RobotState.LINE) {
            wheelsOn = false;
            hatchEngaged = input.getINHatchEngaged();
        }
        
        output();
    }

    @Override
    public void output() {
        if (wheelsOn) {
            intakeWheels.set(0.5);
        } else {
            intakeWheels.set(0);
        }

        if (hatchEngaged) {
            hatchLeft.set(Value.kForward);
            hatchRight.set(Value.kForward);
        } else {
            hatchLeft.set(Value.kReverse);
            hatchRight.set(Value.kReverse);
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

    public static FourBarIntake getInstance() {
        if (instance == null) {
            synchronized (FourBarIntake.class) {
                if (instance == null)
                    instance = new FourBarIntake();
            }
        }
        return instance;
    }
}