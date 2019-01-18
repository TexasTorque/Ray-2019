package org.texastorque.util;

public final class MathUtils {
	
	private MathUtils() { }
	
	public static double calcAreaTrapezoid(double base1, double base2, double height) {
		return 0.5 * (base1 + base2) * height;
	}

	public static double clamp(double value, double min, double max) {
		return Math.min(Math.max(min, value), max);
	}
}
