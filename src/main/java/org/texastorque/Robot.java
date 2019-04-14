package org.texastorque;

import org.texastorque.subsystems.*;
import org.texastorque.auto.AutoManager;
import org.texastorque.inputs.*;
import org.texastorque.inputs.State.RobotState;
import org.texastorque.auto.sequences.PreClimb;

import org.texastorque.torquelib.base.TorqueIterative;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import java.util.ArrayList;

public class Robot extends TorqueIterative {

    private ArrayList<Subsystem> subsystems;
	private Subsystem driveBase = DriveBase.getInstance();
	private Subsystem lift = Lift.getInstance();
	private Subsystem rotary = Rotary.getInstance();
	private Subsystem intake = FourBarIntake.getInstance();
	private Subsystem climber = Climber.getInstance();
	
	private State state = State.getInstance();
	private Input input = Input.getInstance();
	private Feedback feedback = Feedback.getInstance();
	private AutoManager autoManager = AutoManager.getInstance();

	private PreClimb  preClimb;
	public void robotInit() {
		initSubsystems();
		feedback.resetNavX();
		feedback.resetDriveEncoders();

		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(320, 240);
		camera.setFPS(10);
	}

	private void initSubsystems() {
		subsystems = new ArrayList<Subsystem>();
		subsystems.add(driveBase);
		subsystems.add(lift);
		subsystems.add(rotary);
		subsystems.add(intake);
		subsystems.add(climber);
	}

	@Override
	public void autoInit() {
		state.setRobotState(RobotState.AUTO);
		autoManager.chooseSequence();
		feedback.resetDriveEncoders();

		for (Subsystem system : subsystems) {
			system.autoInit();
		}
	}
	
	@Override
	public void teleopInit() {
		state.setRobotState(RobotState.TELEOP);
		preClimb = new PreClimb();

		for (Subsystem system : subsystems) {
			system.teleopInit();
		}
	}

	@Override
	public void disabledInit() {
		for (Subsystem system : subsystems) {
			system.disabledInit();
		}
	}

	@Override
	public void autoContinuous() {
		if (state.getRobotState() == RobotState.AUTO) {
			autoManager.runSequence();
			input.updateState();
		}
		else {
			input.updateControllers();
		}

		for (Subsystem system : subsystems) {
			system.run(state.getRobotState());
		}
	}

	@Override
	public void teleopContinuous() {
		if(state.getRobotState() == RobotState.PRECLIMB){
			preClimb.run();
			input.updateDrive();
			input.updateState();
		}
		else if (state.getRobotState() == RobotState.TELEOP){
			input.updateControllers();
		}
		for (Subsystem system : subsystems) {
			system.run(state.getRobotState());
		}
	}

	@Override
	public void disabledContinuous() {
		for (Subsystem system : subsystems) {
			system.disabledContinuous();
		}
	}

	@Override
	public void alwaysContinuous() {
		feedback.update();
		feedback.smartDashboard();
		
		for (Subsystem system : subsystems) {
			system.smartDashboard();
		}
	}
}