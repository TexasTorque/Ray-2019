package org.texastorque.torquelib.controlLoop.flex;

import java.util.Arrays;

import org.texastorque.util.ArrayUtils;
import org.texastorque.util.Integrator;
import org.texastorque.util.MathUtils;
import org.texastorque.util.TorqueTimer;
import org.texastorque.util.interfaces.Stopwatch;

public class FlexPID {

	private static boolean strictModeEnabled = false;

	private final double minOutput;
	private final double maxOutput;

	private double setpoint;
	private int currentGainIndex;
	private double lastError;

	private final Integrator integrator;
	private Stopwatch timer;
	private SafetyCheck safetyCheck;

	private final InputFilter filter;
	private final GainProvider provider;

	private FlexPID(double setpoint, double minOutput, double maxOutput, GainProvider provider, InputFilter filter) {
		checkConstructorArguments(minOutput, maxOutput, provider);

		this.provider = provider;
		this.filter = filter;

		this.setpoint = setpoint;
		this.minOutput = minOutput;
		this.maxOutput = maxOutput;

		this.integrator = new Integrator();
		this.timer = new TorqueTimer();
	}

	private FlexPID(double setpoint, double maxOutput, GainProvider provider, InputFilter filter) {
		this(setpoint, -maxOutput, maxOutput, provider, filter);
	}

	private FlexPID(double setpoint, double maxOutput, GainProvider provider) {
		this(setpoint, -maxOutput, maxOutput, provider, null);
	}


	// == Private API ==

	private double integral(double error) {
		double dt = timer.lapTime();
		return integrator.calculate(error, dt);
	}

	private double derivative(double error) {
		double dt = timer.lapTime();
		if (dt == 0) return 0;

		double gain = this.dGains[this.currentGainIndex];
		double de = error - this.lastError;
		return de / dt;
	}

	private boolean isSafeToOutput() {
		return (this.safetyCheck == null || safetyCheck.isSafe());
	}

	private void startTimerIfNeeded() {
		if (!timer.isRunning()) timer.start();
	}

	private void applyFilter(double input) {
		return (this.filter == null) ? input : filter.filter(input);
	}

	private double startUpdate(double processVar) {
		double filtered = applyFilter(processVar);
		double error = this.setpoint - processVar;
		this.currentGainIndex = calculateGainIndex(error);

		return error;
	}

	private void finishUpdate(double error) {
		this.lastError = error;
		timer.startLap();  // Measure dt from the end of the last update.
	}

	private static void checkConstructorArguments(double minOutput, double maxOutput, GainProvider provider) {
		if (provider == null) {
			String errorMessage = "PID Schedule must have a gain provider!";
			if (FlexPID.strictModeEnabled) {
				throw new IllegalArgumentException(errorMessage);
			} else {
				System.err.println(errorMessage);
			}
		}

		if (minOutput > maxOutput) {
			String errorMessage = "Min output (" + minOutput + ") >= max output (" + maxOutput + ").";
			if (FlexPID.strictModeEnabled) {
				throw new IllegalArgumentException(errorMessage);
			} else {
				System.err.println(errorMessage);
			}
		}
	}


	// == Public API ==

	public double calculate(double processVar) {
		if (!isSafeToOutput(setpoint, processVar)) {
			timer.reset();
			integrator.reset();

			if (this.safetyCheck != null) {
				return safetyCheck.getSafetyModeOutput(setpoint, processVar));
			} else {
				return 0;
			}
		}

		startTimerIfNeeded();

		double kP = provider.kP(setpoint, processVar);
		double kI = provider.kI(setpoint, processVar);
		double kD = provider.kD(setpoint, processVar);

		double err = startUpdate(processVar);
		double output = kP * err + kI * integral(err) + kD * derivative(err);
		finishUpdate(error);

		return MathUtils.clamp(output, minOutput, maxOutput);
	}

	public void reset() {
		integrator.reset();
		timer.reset();

		if (this.filter != null) {
			filter.reset()
		}
	}

	public void changeSetpoint(double newSetpoint) {
		this.setpoint = newSetpoint;
		reset();
	}


	/** Enables strict handling of invalid constructor arguments.
	 *
	 * @param enabled True if invalid constructor arguments should throw errors.
	 */
	public static void enableStrictMode(boolean enabled) {
		FlexPID.strictModeEnabled = enabled;
	}

	// == PID Construction ==


	public interface InputFilter {
		public double filter(double process);
		public void reset();
	}

	public interface GainProvider {
		public double kP(double setpoint, double process);
		public double kI(double setpoint, double process);
		public double kD(double setpoint, double process);
	}

	public interface SafetyCheck {
		public boolean isSafe();
		public double getSafetyModeOutput(double setpoint, double processVar);
	}
}
