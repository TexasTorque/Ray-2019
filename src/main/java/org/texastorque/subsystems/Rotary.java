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
    private boolean rotaryTestF;
    private boolean rotaryTestB;

    private Rotary() {
        rotary = new TorqueMotor(new VictorSP(Ports.RT_MOTOR), clockwise);

        speed = 0;
        setpoint = input.getRTSetpoint(0);

        this.rotaryPID = new ScheduledPID.Builder(setpoint, -0.8, 0.1, 2)
                .setRegions(0)
                .setPGains(0.025, 0)
                .setIGains(0.01, 0)
                // .setDGains(0.0)

                // ku = 0.5;
                // pu = 0.54;
                // .setPGains(0.225) // 0.45 * ku
                // .setIGains(0.5) // 1.2 * kp / pu/ 8
                .build();
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
    public void disabledContinuous() {
        runRotaryPID(0);
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
            runRotaryPID(1);
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

    private void runRotaryPID(int position) {
        setpoint = input.getRTSetpoint(position);
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