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
    private DoubleSolenoid hatchTusk;

    private double wheelSpeed;
    private boolean tuskEngaged;

    private boolean clockwise = true;

    private Intake() {
        intakeWheels = new TorqueMotor(new VictorSP(Ports.IN_MOTOR), clockwise);

        hatchTusk = new DoubleSolenoid(0, Ports.IN_HATCH_SOLE_A, Ports.IN_HATCH_SOLE_B);
    }

    @Override
    public void autoInit() {
        wheelSpeed = 0;
    }

    @Override
    public void teleopInit() {
        wheelSpeed = 0;
    }

    @Override
    public void disabledInit() {
        wheelSpeed = 0;
    }

    @Override
    public void run(RobotState state) {
        if (state == RobotState.AUTO) {
            if (input.getINActive()) {
                if (input.getHatchState()) {
                    wheelSpeed = 0.5;
                } else {
                    wheelSpeed = -0.5;
                }
            } 
            else {
                if (input.getHatchState()) {
                    wheelSpeed = 0.08;
                } else {
                    wheelSpeed = -0.08;
                }
            }
        }

        else if (state == RobotState.TELEOP) {
            if (input.getINActive()) {
                if (input.getHatchState()) {
                    wheelSpeed = 0.5;
                } else {
                    wheelSpeed = -0.5;
                }
            } 
            else {
                if (input.getHatchState()) {
                    wheelSpeed = 0.08;
                } else {
                    wheelSpeed = -0.08;
                }
            }

            tuskEngaged = input.getINTuskEngaged();
        }

        else if (state == RobotState.VISION) {
            wheelSpeed = 0;
        }

        else if (state == RobotState.LINE) {
            wheelSpeed = 0;
        }
        
        output();
    }

    @Override
    public void output() {
        intakeWheels.set(wheelSpeed);

        if (tuskEngaged) {
            hatchTusk.set(Value.kReverse);
        } else {
            hatchTusk.set(Value.kForward);
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