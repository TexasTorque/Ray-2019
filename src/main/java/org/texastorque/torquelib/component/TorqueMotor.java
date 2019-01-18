package org.texastorque.torquelib.component;

//import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * Generic Motor class that also provides linearization for IFI/Vex Pro 88x
 * motor controllers. Talons, Jaguars, and Victor SP's are basically perfect and
 * do not need linearization.
 */
public class TorqueMotor {

	private SpeedController controller;
	private boolean reverse;

	private LinearizationType linearizer;

	private double previousSpeed;

	/**
	 * Create a new motor.
	 *
	 * @param sc
	 *            The SpeedController object.
	 * @param rev
	 *            Whether or not the motor is reversed.
	 *            <p/>
	 *            SpeedController is an interface implemented by Victor, Talon,
	 *            Jaguar.
	 * @see edu.wpi.first.wpilibj.SpeedController
	 */
	public TorqueMotor(SpeedController sc, boolean rev) {
		this(sc, rev, LinearizationType.kNone);
	}

	/**
	 * Create a new motor.
	 *
	 * @param sc
	 *            The SpeedController object.
	 * @param rev
	 *            Whether or not the motor is reversed.
	 * @param linType
	 *            The linearization method to be used.
	 *            <p/>
	 *            SpeedController is an interface implemented by Victor, Talon,
	 *            Jaguar.
	 * @see edu.wpi.first.wpilibj.SpeedController
	 */
	public TorqueMotor(SpeedController sc, boolean rev, LinearizationType linType) {
		controller = sc;
		reverse = rev;

		linearizer = linType;
	}

	/**
	 * Set the speed of the motor.
	 *
	 * @param speed
	 *            The speed to be set to the output.
	 */
	public void set(double speed) {

		if (speed > 1) {
			speed = 1;
		} else if (speed < -1) {
			speed = -1;
		}

		speed = linearizer.linearize(speed);

		if (speed != previousSpeed) {

			if (reverse) {
				speed *= -1;
			}
			controller.set(speed);

			previousSpeed = speed;
		}
	}

	/**
	 * Specifies the logistic fits used for linearization. This data was
	 * obtained experimentally using some old controllers I had laying around.
	 * The results are not perfect but it's a lot closer to linear than the
	 * normal behaviour.
	 *
	 */
	public enum LinearizationType {

		k888(1.000720771, 6.395094471, -24.892191226, 12.465463138, 1.25, true), k884(1.130283678, -10.497754480,
				24.996120969, -12.608256373, 5.5, true), kNone(0, 0, 0, 0, 0, false);

		double m_A;
		double m_B;
		double m_C;
		double m_D;

		double m_deadband;

		boolean doLinearize;

		private LinearizationType(double A, double B, double C, double D, double deadband, boolean linearize) {
			m_A = A;
			m_B = B;
			m_C = C;
			m_D = D;

			doLinearize = linearize;

			m_deadband = deadband;
		}

		public double linearize(double in) {

			if (doLinearize) {
				if (Math.abs(in) < 0.01) {
					// Dont bother for really small inputs.
					return 0.0;
				} else if (in > 0.0) {
					in = (1 - m_deadband) * in + m_deadband;
				} else if (in < 0.0) {
					in = (1 - m_deadband) * in - m_deadband;
				}

				// Uses the inverse of the logistic fit we did on raw data to
				// find
				// the signal value needed to ouput the desired voltage.
				double out = m_C / (12.8 * in - m_D);
				out = out - 1;
				out = out / m_A;
				out = Math.log(out);
				out = out / m_B;

				return out;
			} else {
				return in;
			}
		}
	}
}
