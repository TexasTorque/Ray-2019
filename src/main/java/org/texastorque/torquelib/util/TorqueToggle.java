package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Timer;

public class TorqueToggle {

	private boolean toggle;
	private boolean lastCheck;

	public TorqueToggle() {
		toggle = false;
		lastCheck = false;
	}
	
	public TorqueToggle(boolean override) {
		toggle = override;
	}

	public void calc(boolean current) {
		// Checks for an edge in boolean state. We only want to perform an
		// action once when we go from False to True
		if (current != lastCheck) {
			// If the value is true now, it is the first time it is true. Flip
			// the toggle.
			if (current) {
				toggle = !toggle;
				System.out.println("IT FLIPPED");
			}
			// Keep track of the previous value. Does not need to be updated if
			// lastCheck is already equal to current.
			lastCheck = current;
		}
	}

	public void set(boolean override) {
		toggle = override;
	}

	public boolean get() {
		return toggle;
	}
}
