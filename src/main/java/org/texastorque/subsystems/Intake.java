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
    private DoubleSolenoid hatchClaw;
    private DoubleSolenoid extender;

    private double wheelSpeed;
    private boolean clawEngaged;
    private boolean clawExtended;

    private boolean clockwise = true;

    private Intake() {
        intakeWheels = new TorqueMotor(new VictorSP(Ports.IN_MOTOR), clockwise);

        hatchClaw = new DoubleSolenoid(0, Ports.IN_HATCH_SOLE_A, Ports.IN_HATCH_SOLE_B);
        extender = new DoubleSolenoid(0, Ports.IN_EXTEND_SOLE_A, Ports.IN_EXTEND_SOLE_B);
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
                    wheelSpeed = 1.0;
                } else {
                    wheelSpeed = -0.8;
                }
            } 
            else {
                if (input.getHatchState()) {
                    wheelSpeed = 0.15;
                } else {
                    wheelSpeed = -0.15;
                }
            }

            clawEngaged = input.getINClawEngaged();
            clawExtended = input.getINClawExtended();
        }

        else if (state == RobotState.TELEOP) {
            if (input.getINActive()) {
                if (input.getHatchState()) {
                    wheelSpeed = 1.0;
                } else {
                    wheelSpeed = -0.8;
                }
            } 
            else {
                if (input.getHatchState()) {
                    wheelSpeed = 0.15;
                } else {
                    wheelSpeed = -0.15;
                }
            }

            clawEngaged = input.getINClawEngaged();
            clawExtended = input.getINClawExtended();
        }

        else if (state == RobotState.VISION) {
            if (input.getINActive()) {
                if (input.getHatchState()) {
                    wheelSpeed = 1.0;
                } else {
                    wheelSpeed = -0.8;
                }
            } 
            else {
                if (input.getHatchState()) {
                    wheelSpeed = 0.15;
                } else {
                    wheelSpeed = -0.15;
                }
            }

            clawEngaged = input.getINClawEngaged();
            clawExtended = input.getINClawExtended();
        }

        else if (state == RobotState.LINE) {
            wheelSpeed = 0;
        }
        
        output();
    }

    @Override
    public void output() {
        intakeWheels.set(wheelSpeed);

        if (clawEngaged) {
            hatchClaw.set(Value.kForward);
        } else {
            hatchClaw.set(Value.kReverse);
        }

        if (clawExtended) {
            extender.set(Value.kForward);
        } else {
            extender.set(Value.kReverse);
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