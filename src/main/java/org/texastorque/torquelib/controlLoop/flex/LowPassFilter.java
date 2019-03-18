package org.texastorque.torquelib.controlLoop.flex;

public class LowPassFilter implements InputFilter {

	private final double alpha;
	private double lastValue;

	public LowPassFilter(double alpha) {
		checkAlpha(alpha);

		this.alpha = (alpha <= 0 || alpha > 1) ? 1 : alpha;
	}

	@Override
	public double filter(double input) {
		this.lastValue = this.alpha * input + (1 - this.alpha) * this.lastValue;
		return this.lastValue;
	}

	@Override
	public void reset() {
		this.lastValue = 0;
	}

	private static void checkAlpha(double alpha) {
		if (alpha <= 0 || alpha > 1) {
			System.err.println("Low pass alpha should be chosen such that 0 <= alpha < 1.")
		}
	}
}