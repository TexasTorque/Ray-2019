package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

import edu.wpi.first.wpilibj.VictorSP;

public class Lift extends Subsystem {

    private static volatile Lift instance;
    private RobotState currentState;

    private TorqueMotor motor;

    private final ScheduledPID liftPID;
    private double speed;
    private double currentPos;
    private double setpoint;
    private double prevSetpoint;
    private boolean clockwise;

    private Lift() {
        motor = new TorqueMotor(new VictorSP(Ports.LF_MOTOR), clockwise);

        this.liftPID = new ScheduledPID.Builder(0, 0.5)
                .setPGains(0.01)
                .setIGains(0.01)
                .setDGains(0.01)
                .build();

        speed = 0;
        setpoint = input.getLFSetpoint(0);
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
            runLiftPID();
        }
        else if (currentState == RobotState.VISION) {
            runLiftBottom();
        }
        else if (currentState == RobotState.LINE) {
            runLiftPID();
        }
        
        output();
    }

    private void runLiftPID() {
        setpoint = input.getLFSetpoint();
            currentPos = feedback.getLFPosition();
            if (setpoint != prevSetpoint) {
                liftPID.changeSetpoint(setpoint);
                prevSetpoint = setpoint;
            }

            speed = liftPID.calculate(currentPos);
            output();
    }

    private void runLiftBottom() {
        setpoint = input.getLFSetpoint(0);
            currentPos = feedback.getLFPosition();
            if (setpoint != prevSetpoint) {
                liftPID.changeSetpoint(setpoint);
                prevSetpoint = setpoint;
            }

            speed = liftPID.calculate(currentPos);
            output();
    }

    @Override
    protected void output() {
        motor.set(speed);
    }

    @Override
    public void smartDashboard() {}

    public static Lift getInstance() {
        if (instance == null) {
            synchronized (Lift.class) {
                if (instance == null)
                    instance = new Lift();
            }
        }
        return instance;
    }
}