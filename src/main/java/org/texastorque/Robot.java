package org.texastorque;

import org.texastorque.subsystems.*;
import org.texastorque.auto.AutoManager;
import org.texastorque.inputs.*;
import org.texastorque.inputs.State.RobotState;

import org.texastorque.torquelib.base.TorqueIterative;

import java.util.ArrayList;

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
		initSubsystems();

		autoManager.displayChoices();
	}

	private void initSubsystems() {
		subsystems = new ArrayList<>();
		subsystems.add(driveBase);
		subsystems.add(lift);
		subsystems.add(rotary);
		subsystems.add(intake);
		subsystems.add(climber);
	}

	@Override
	public void autoInit() {
		autoManager.chooseSequence();

		for (Subsystem system : subsystems) {
			system.autoInit();
		}
	}
	
	@Override
	public void teleopInit() {
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
		input.updateControllers();
		for (Subsystem system : subsystems) {
			system.run(state.getRobotState());
		}
	}

	@Override
	public void alwaysContinuous() {
		feedback.update();
		feedback.smartDashboard();
		for (Subsystem system : subsystems) {
			system.run(state.getRobotState());
		}
	}

	@Override
	public void disabledContinuous() {
	}
}