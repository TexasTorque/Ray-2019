package org.texastorque.torquelib.controlLoop.flex;

class SimpleGains implements GainProvider {

	private final double kP, kI, kD;

	public SimpleGains(double kP, double kI, double kD) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
	}

	public double kP(double setpoint, double process) {
		return this.kP;
	}

	public double kI(double setpoint, double process) {
		return this.kI;
	}

	public double kD(double setpoint, double process) {
		return this.kD;
	}
}