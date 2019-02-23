package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

import edu.wpi.first.wpilibj.VictorSP;

public class Climber extends Subsystem {

    private static volatile Climber instance;

    private TorqueMotor leftTom;
    private TorqueMotor rightTom;
	private TorqueMotor rearA;
    private TorqueMotor rearB;
    
    private final ScheduledPID rearPID;
    private double tomSpeed = 0;
    private double rearSpeed = 0;
    private boolean clockwise = true;

    private Climber() {
        leftTom = new TorqueMotor(new VictorSP(Ports.CM_LEFT_TOM_MOTOR), clockwise);
        rightTom = new TorqueMotor(new VictorSP(Ports.CM_RIGHT_TOM_MOTOR), clockwise);
        rearA = new TorqueMotor(new VictorSP(Ports.CM_REAR_A_MOTOR), clockwise);
        rearB = new TorqueMotor(new VictorSP(Ports.CM_REAR_B_MOTOR), clockwise);

        rearPID = new ScheduledPID.Builder(0, 0, 0.7, 1)
            .setPGains(0.1)
            .setIGains(0)
            .setDGains(0)
            .build();
    }

    @Override
    public void autoInit() {
        tomSpeed = 0;
        rearSpeed = 0;
    }

    @Override
    public void teleopInit() {
        tomSpeed = 0;
        rearSpeed = 0;
    }

    @Override
    public void disabledInit() {
        tomSpeed = 0;
        rearSpeed = 0;
    }

    @Override
    public void run(RobotState state) {
        if (state == RobotState.AUTO) {
        }

        else if (state == RobotState.TELEOP) {
            if (input.getCMEnabled()) {
                tomSpeed = 0.1;
                // rearSpeed = 0.1;
                double currentPitch = feedback.getPitch();
                rearSpeed = rearPID.calculate(currentPitch);
            }
            else {
                tomSpeed = 0;
                rearSpeed = 0;
            }
        }

        else if (state == RobotState.VISION) {}

        else if (state == RobotState.LINE) {}
        
        output();
    }

    @Override
    public void output() {
        // leftTom.set(tomSpeed);
        // rightTom.set(tomSpeed);
        // rearA.set(rearSpeed);
        // rearB.set(rearSpeed);
    }

    @Override
    public void disabledContinuous() {}

    @Override
    public void autoContinuous() {}

    @Override
    public void teleopContinuous() {}

    @Override
    public void smartDashboard() {}

    public static Climber getInstance() {
        if (instance == null) {
            synchronized (Climber.class) {
                if (instance == null)
                    instance = new Climber();
            }
        }
        return instance;
    }
}