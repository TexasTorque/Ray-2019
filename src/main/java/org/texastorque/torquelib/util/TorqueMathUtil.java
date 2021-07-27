package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Timer;

public class TorqueMathUtil {

	public static double constrain(double value, double absMax) {
		if (value > absMax) {
			return absMax;
		} else if (value < -absMax) {
			return -absMax;
		} else {
			return value;
		}
	}

	public static double arrayClosest(double[] values, double value) {
		double closest = 0.0;
		for (int i = 0; i < values.length; i++) {
			if (Math.abs(values[i] - value) < Math.abs(closest - value)) {
				closest = value;
			}
		}
		return closest;
	}

	public static boolean near(double number, double value, double howClose) {
		return Math.abs(number - value) < howClose;
	}

	public static double addSign(double value, double add) {
		if (value < 0) {
			return value - add;
		} else {
			return value + add;
		}
	}
	
	public static void delay(double delay) {
		double startTime = Timer.getFPGATimestamp();
		while(startTime + delay >= Timer.getFPGATimestamp()) {
			if(Timer.getFPGATimestamp() - startTime > 10) {
				break;
			}
		}
	} //returns true once delayed
}
