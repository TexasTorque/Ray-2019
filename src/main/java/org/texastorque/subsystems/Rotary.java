package org.texastorque.subsystems;
import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

import edu.wpi.first.wpilibj.VictorSP;

public class Rotary extends Subsystem {

    private static volatile Rotary instance;
    
    private TorqueMotor rotary;

    private final ScheduledPID rotaryPID;
    private double speed;
    private double currentPos;
    private double setpoint;
    private double prevSetpoint;
    private boolean clockwise = true;

    private Rotary() {
        rotary = new TorqueMotor(new VictorSP(Ports.RT_MOTOR), clockwise);

        this.rotaryPID = new ScheduledPID.Builder(0, 0.5)
                .setPGains(0.01)
                .setIGains(0.01)
                .setDGains(0.01)
                .build();

        speed = 0;
        setpoint = input.getRTSetpoint(0);
    }

    @Override
    public void autoInit() {
        speed = 0;
    }

    @Override
    public void teleopInit() {
        speed = 0;
    }

    @Override
    public void disabledInit() {
        speed = 0;
        output();
    }

    @Override
    public void run(RobotState state) {
        if (state == RobotState.AUTO) {
        }

        else if (state == RobotState.TELEOP) {
            runRotaryPID();
        }

        else if (state == RobotState.VISION) {
            runRotaryBottom();
        }

        else if (state == RobotState.LINE) {
            runRotaryPID();
        }
        
        output();
    }

    private void runRotaryPID() {
        setpoint = input.getRTSetpoint();
        currentPos = feedback.getRTPosition();
        if (setpoint != prevSetpoint) {
            rotaryPID.changeSetpoint(setpoint);
            prevSetpoint = setpoint;
        }

        speed = rotaryPID.calculate(currentPos);
    }

    private void runRotaryBottom() {
        setpoint = input.getRTSetpoint(0);
        currentPos = feedback.getRTPosition();
        if (setpoint != prevSetpoint) {
            rotaryPID.changeSetpoint(setpoint);
            prevSetpoint = setpoint;
        }

        speed = rotaryPID.calculate(currentPos);
    }

    @Override
    public void output() {
        rotary.set(speed);
    }

    @Override
    public void disabledContinuous() {}

    @Override
    public void autoContinuous() {}

    @Override
    public void teleopContinuous() {}

    @Override
    public void smartDashboard() {}

    public static Rotary getInstance() {
        if (instance == null) {
            synchronized (Rotary.class) {
                if (instance == null)
                    instance = new Rotary();
            }
        }
        return instance;
    }
}