package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

import edu.wpi.first.wpilibj.VictorSP;

public class FourBarIntake extends Subsystem {

    private static volatile FourBarIntake instance;
    
    private TorqueMotor intakeWheels;
    private DoubleSolenoid hatchClaw;
    private DoubleSolenoid hatchExtend;

    private double wheelSpeed;
    private boolean clawEngaged;
    private boolean clawExtended;

    private boolean clockwise = true;

    private FourBarIntake() {
        intakeWheels = new TorqueMotor(new VictorSP(Ports.IN_MOTOR), clockwise);

        hatchExtend = new DoubleSolenoid(0, Ports.IN_HATCH_SOLE_A, Ports.IN_HATCH_SOLE_B);
        hatchClaw = new DoubleSolenoid(0, Ports.IN_HATCH_EXT_SOLE_A, Ports.IN_HATCH_EXT_SOLE_B);
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
        }

        else if (state == RobotState.TELEOP || state == RobotState.PRECLIMB) {
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
        if (clawExtended){
            hatchExtend.set(Value.kForward);
        }
        else{
            hatchExtend.set(Value.kReverse);
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