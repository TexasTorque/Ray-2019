package org.texastorque;

import org.texastorque.subsystems.*;
import org.texastorque.auto.AutoManager;
import org.texastorque.inputs.*;
import org.texastorque.inputs.State.RobotState;

import org.texastorque.torquelib.base.TorqueIterative;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Robot extends TorqueIterative {

    private ArrayList<Subsystem> subsystems;
	private Subsystem driveBase = DriveBase.getInstance();
	private Subsystem lift = Lift.getInstance();
	private Subsystem rotary = Rotary.getInstance();
	private Subsystem intake = Intake.getInstance();
	private Subsystem climber = Climber.getInstance();
	
	private State state = State.getInstance();
	private Input input = Input.getInstance();
	private Feedback feedback = Feedback.getInstance();
	private AutoManager autoManager = AutoManager.getInstance();

	public void robotInit() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Init time: " + dateFormat.format(date));

		initSubsystems();
		feedback.resetNavX();
		feedback.resetDriveEncoders();

		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(320, 240);
		camera.setFPS(16);
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

			if (autoManager.sequenceEnded()) {
				state.setRobotState(RobotState.TELEOP);
			}
		}
		else if (state.getRobotState() == RobotState.DB_ONLY) {
			autoManager.runSequence();
			input.updateState();
			input.updateDrive();

			if (autoManager.sequenceEnded()) {
				state.setRobotState(RobotState.TELEOP);
			}
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
		if (state.getRobotState() == RobotState.DB_ONLY) {
			autoManager.runSequence();
			input.updateState();
			input.updateDrive();

			if (autoManager.sequenceEnded()) {
				state.setRobotState(RobotState.TELEOP);
			}
		}
		else {
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