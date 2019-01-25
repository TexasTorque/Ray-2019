package org.texastorque;

import org.texastorque.subsystems.*;
import org.texastorque.inputs.*;

import org.texastorque.torquelib.base.TorqueIterative;

import java.util.ArrayList;

public class Robot extends TorqueIterative {

    private ArrayList<Subsystem> subsystems;
	private Subsystem driveBase = DriveBase.getInstance();

	private Input input = Input.getInstance();

	public void robotInit() {
		initSubsystems();

		for (Subsystem system : subsystems) {
			system.autoInit();
		}
	}

	private void initSubsystems() {
		subsystems = new ArrayList<>();
		subsystems.add(driveBase);
	}


	public void disabledInit() {
		for (Subsystem system : subsystems) {
			system.disabledInit();
		}
	}

	public void teleopInit() {
		for (Subsystem system : subsystems) {
			system.teleopInit();
		}
	}

	@Override
	public void autoContinuous() {
	}

	@Override
	public void teleopContinuous() {
		input.update();
		for (Subsystem system : subsystems) {
			system.teleopContinuous();
		}
	}

	@Override
	public void alwaysContinuous() {
		for (Subsystem system : subsystems) {
			system.smartDashboard();
		}
	}

	@Override
	public void disabledContinuous() {
	}
}