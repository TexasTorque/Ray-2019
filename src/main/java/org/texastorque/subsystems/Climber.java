package org.texastorque.subsystems;

import org.texastorque.inputs.State.RobotState;
import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueVictor;
import org.texastorque.torquelib.controlLoop.ScheduledPID;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber extends Subsystem {

    private static volatile Climber instance;

    private TorqueVictor leftTom;
    private TorqueVictor rightTom;
    private TorqueVictor rearA;
    private TorqueVictor rearB;

    private final ScheduledPID rearPID;
    private double tomSpeed = 0;
    private double rearSpeed = 0;
    private boolean clockwise = true;

    private Climber() {
        leftTom = new TorqueVictor(Ports.CM_LEFT_TOM_MOTOR, !clockwise);
        rightTom = new TorqueVictor(Ports.CM_RIGHT_TOM_MOTOR, clockwise);
        rearA = new TorqueVictor(Ports.CM_REAR_A_MOTOR, clockwise);
        rearB = new TorqueVictor(Ports.CM_REAR_B_MOTOR, clockwise);

        tomSpeed = 0;
        rearSpeed = 0;

        rearPID = new ScheduledPID.Builder(0, -1.0, 1.0, 1).setPGains(0.25)
                // .setIGains(0)
                // .setDGains(0)
                .build();

    }

    @Override
    public void autoInit() {
        tomSpeed = 0;
        rearSpeed = 0;
        rearPID.changeSetpoint(feedback.getPitch());
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
        if (state == RobotState.AUTO || state == RobotState.DB_ONLY) {
            tomSpeed = input.getCMTomSpeed();
            rearSpeed = 0;
        }

        else if (state == RobotState.TELEOP) {
            if (input.getCMEnabled()) {
                tomSpeed = 0.6;

                if (feedback.getCMAtBottom()) {
                    rearSpeed = 0;
                } else {
                    double currentPitch = feedback.getPitch();
                    rearSpeed = rearPID.calculate(-currentPitch);
                }
            } else if (input.getCMRetract()) {
                tomSpeed = 0;
                rearSpeed = -0.3;
            } else {
                tomSpeed = input.getCMTomSpeed();
                rearSpeed = 0;
            }

            // tomSpeed = input.CM_tomSpeed;
            // rearSpeed = input.CM_rearSpeed;
        }

        else if (state == RobotState.VISION) {
            tomSpeed = 0;
            rearSpeed = 0;
        }

        else if (state == RobotState.LINE) {
            tomSpeed = 0;
            rearSpeed = 0;
        }

        output();
    }

    @Override
    public void output() {
        leftTom.set(tomSpeed);
        rightTom.set(tomSpeed);
        rearA.set(rearSpeed);
        rearB.set(rearSpeed);
    }

    @Override
    public void disabledContinuous() {
    }

    @Override
    public void autoContinuous() {
    }

    @Override
    public void teleopContinuous() {
    }

    @Override
    public void smartDashboard() {
        SmartDashboard.putBoolean("CM_enabled", input.getCMEnabled());
    }

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