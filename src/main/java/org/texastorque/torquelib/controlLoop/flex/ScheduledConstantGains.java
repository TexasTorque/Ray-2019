package org.texastorque.torquelib.controlLoop.flex;

import java.util.Arrays;

import org.texastorque.util.ArrayUtils;

class ScheduledConstantGains implements GainProvider {

	private final double[] gainDivisions;
	private final double[] pGains;
	private final double[] iGains;
	private final double[] dGains;

	public SimpleGains(double[] kP, double[] kI, double[] kD, double[] divisions) {
		this.pGains = kP;
		this.iGains = kI;
		this.dGains = kD;
		this.gainDivisions = divisions;
	}

	public double kP(double setpoint, double process) {
		int i = calculateGainIndex(setpoint, process);
		return this.pGains[i];
	}

	public double kI(double setpoint, double process) {
		int i = calculateGainIndex(setpoint, process);
		return this.iGains[i];
	}

	public double kD(double setpoint, double process) {
		int i = calculateGainIndex(setpoint, process);
		return this.dGains[i];
	}


	/** Calculates the index for the current gain values.
	 *
	 * @param error The current error in the process variable.
	 * @return The integer index for the gain values.
	 */
	private int calculateGainIndex(double setpoint, double process) {
		double error = setpoint - process;

		if (gainDivisions.length == 0 || error <= this.gainDivisions[0]) {
			return 0;
		}

		for (int i = 0; i < gainDivisions.length - 1; i++) {
			double leftBound = this.gainDivisions[i];
			double rightBound = this.gainDivisions[i + 1];

			if (leftBound <= error && error < rightBound) {
				return i + 1;
			}
		}

		return gainDivisions.length;
	}

	// == Gain Construction ==

	public static class Builder {

		private final double[] gainDivisions;
		private final double[] pGains;
		private final double[] iGains;
		private final double[] dGains;

		public Builder(int count) {
			this.pGains = new double[count];
			this.iGains = new double[count];
			this.dGains = new double[count];
			this.gainDivisions = new double[count - 1];
		}

		public Builder setRegions(double... regions) {
			ArrayUtils.bufferAndFill(regions, this.gainDivisions);

			// Divisions should always be specified in ascending order.
			if (!ArrayUtils.isSorted(regions, true)) {
				Arrays.sort(this.gainDivisions);
				System.out.println("Gain schedule was not ordered correctly.");
			}

			return this;
		}

		public Builder setPGains(double... pGains) {
			ArrayUtils.bufferAndFill(pGains, this.pGains);
			return this;
		}

		public Builder setIGains(double... iGains) {
			ArrayUtils.bufferAndFill(iGains, this.iGains);
			return this;
		}

		public Builder setDGains(double... dGains) {
			ArrayUtils.bufferAndFill(dGains, this.dGains);
			return this;
		}

		public ScheduledPID build() {
			return new ScheduledConstantGains(this.pGains, this.iGains, this.dGains, this.gainDivisions);
		}
	}
}