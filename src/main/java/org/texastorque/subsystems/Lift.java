package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lift extends Subsystem {

    private static volatile Lift instance;

    private TorqueMotor pulleyA;
    private TorqueMotor pulleyB;

    private final ScheduledPID liftPID;
    private double speed;
    private double currentPos;
    private double setpoint;
    private double prevSetpoint;
    private boolean clockwise = true;

    private Lift() {
        pulleyA = new TorqueMotor(new VictorSP(Ports.LF_MOTOR_A), clockwise);
        pulleyB = new TorqueMotor(new VictorSP(Ports.LF_MOTOR_B), clockwise);

        speed = 0;
        setpoint = input.calcLFSetpoint(0);

        liftPID = new ScheduledPID.Builder(setpoint, -0.2, 0.8, 2)
                .setRegions(0)
                .setPGains(0.6, 1.0)
                //.setIGains(0.0, 0.5) //0, 0.5
                //.setDGains(0.01)
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
    }

    @Override
    public void run(RobotState state) {
        if (state == RobotState.AUTO) {
            runLiftPID();
        }

        else if (state == RobotState.TELEOP) {
            if (input.getLFManualMode()) {
                speed = input.getLFManualOutput();
            } else {
                runLiftPID();
            }
        }

        else if (state == RobotState.VISION) {
            if (input.getLFManualMode()) {
                speed = input.getLFManualOutput();
            } else {
                runLiftPID(0);
            }
        }

        else if (state == RobotState.LINE) {
            runLiftPID();
        }
        
        output();
    }

    private void runLiftPID() {
        setpoint = input.calcLFSetpoint();
        currentPos = feedback.getLFPosition();
        if (setpoint != prevSetpoint) {
            liftPID.changeSetpoint(setpoint);
            prevSetpoint = setpoint;
        }

        speed = liftPID.calculate(currentPos);
    }

    private void runLiftPID(int position) {
        setpoint = input.calcLFSetpoint(position);
        currentPos = feedback.getLFPosition();
        if (setpoint != prevSetpoint) {
            liftPID.changeSetpoint(setpoint);
            prevSetpoint = setpoint;
        }

        speed = liftPID.calculate(currentPos);
    }

    private double addBaseOutput(double speed) {
        if (feedback.getLFPosition() < input.calcLFSetpoint(1)) {
            return speed + 0.05;
        }
        else if (feedback.getLFPosition() < input.calcLFSetpoint(2)) {
            return speed + 0.08;
        }
        return speed;
    }

    private double startTime = Timer.getFPGATimestamp();
    private double stallTime = 5.0;
    private boolean isStalling = false;

    @Override
    protected void output() {
        // speed = addBaseOutput(speed);

        // Check if motor is stalling
        if (Math.abs(speed) < 0.3) {
            isStalling = false;
            startTime = Timer.getFPGATimestamp();
        }
        if (Timer.getFPGATimestamp() - startTime > stallTime) {
            isStalling = true;
            // speed = 0
        }

        pulleyA.set(speed);
        pulleyB.set(speed);
    }

    @Override
    public void disabledContinuous() {}

    @Override
    public void autoContinuous() {}

    @Override
    public void teleopContinuous() {}

    @Override
    public void smartDashboard() {
        SmartDashboard.putNumber("LF_setpoint", setpoint);
        SmartDashboard.putNumber("LF_output", speed);
        SmartDashboard.putBoolean("LF_stall", isStalling);
    }

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
