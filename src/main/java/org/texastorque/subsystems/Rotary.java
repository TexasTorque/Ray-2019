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

    private boolean rotTestBool = false;
    private double rotTestSpeed = 0.0;

    private Rotary() {
        rotary = new TorqueMotor(new VictorSP(Ports.RT_MOTOR), !clockwise);

        this.rotaryPID = new ScheduledPID.Builder(0, 0.5)
                .setPGains(0.026) // keep increasing by small increments (NEEDS MORE TUNING)
                .setIGains(0.0) // get it until it vibrates then take 2/3 
                .setDGains(0.0)
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
    }

    @Override
    public void run(RobotState state) {
        rotTestBool = input.getTempRotBool();
        rotTestSpeed = input.getTempRotSpeed();
        
        if (state == RobotState.AUTO) {
            runRotaryPID();
        }

        else if (state == RobotState.TELEOP) {
            if (rotTestBool) {
                speed = rotTestSpeed;
            } 
            else {
                runRotaryPID();
            }
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
        System.out.println("Setpoint" + setpoint);
        currentPos = feedback.getRTPosition();
        if (setpoint != prevSetpoint) {
            rotaryPID.changeSetpoint(setpoint);
            prevSetpoint = setpoint;
        }

        speed = rotaryPID.calculate(currentPos);
        System.out.println("Speed" + speed);
    
        // rotaryTestF = input.getRTForward();
        // rotaryTestB = input.getRTBackward();

        // if (rotaryTestF) {
        //     speed = .3;
        // }
        // else if (rotaryTestB) {
        //     speed = -.3;
        // } else {
        //     speed =0;
        // }

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
        rotary.set(speed + 0.07);
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