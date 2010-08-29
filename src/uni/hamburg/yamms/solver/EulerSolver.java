package uni.hamburg.yamms.solver;

import java.util.Random;

import uni.hamburg.yamms.math.RealScalarField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.physics.Constants;

/**
 * Implementation of the Euler integration scheme with support for thermal
 * activation via an fluctuating field.
 * <p>
 * You better use the Heun integrator, since this integrator was only built for
 * demonstration purposes.
 * 
 * @author Claas Abert
 * 
 */
public class EulerSolver extends StochasticSolver {
	/** <code>true</code> if drift term should be added */
	protected boolean _drift;

	/**
	 * Standard constructor
	 * 
	 * @param stepSize
	 *            the step size used for integration
	 * @param seed
	 *            the seed for the random generation of the fluctuating field
	 * @param temperature
	 *            the temperature in K
	 * @param drift
	 *            if <code>true</code> the drift term for the stratonovish
	 *            stochastic integral interpretation is added
	 */
	public EulerSolver(double stepSize, long seed, double temperature, boolean drift) {
		super();
		_stepSize = stepSize;
		_temperature = temperature;
		_random = new Random(seed);
		_drift = drift;
	}

	/**
	 * Alternative constructor. The drift term is activated
	 * 
	 * @param stepSize
	 *            the step size used for integration
	 * @param seed
	 *            the seed for the random generation of the fluctuating field
	 * @param temperature
	 *            the temperature in K
	 */
	public EulerSolver(double stepSize, long seed, double temperature) {
		this(stepSize, seed, temperature, true);
	}

	/**
	 * Performs a single integration step and returns the new magnetization
	 * 
	 * @param m
	 *            the current magnetization
	 * @param t
	 *            the current integration time
	 * @param step
	 *            the current integration step
	 * @return the resulting magnetization
	 */
	protected RealVectorField step(RealVectorField m, double t) {
		RealVectorField dW = generateDW();

		RealVectorField dm;
		if (_drift) {
			dm = _model.calculateDerivative(_currentState.derive(m, null, t)).add(drift(m)).times(_stepSize);
		} else {
			dm = _model.calculateDerivative(_currentState.derive(m, null, t)).times(_stepSize);
		}
		dm = dm.add(wienerIncrement(m, dW));
		RealVectorField mDot = dm.times(1/_stepSize); // TODO lazy initialization?
		
		_currentState.step(m, mDot, t);
		callHandlers();

		return m.add(dm).normTo(_model.getMs());
	}

	/**
	 * Calculates the drift term needed for the stratonovish integral
	 * interpretation
	 * 
	 * @param m
	 *            the magnetization
	 * @return the drift term
	 */
	private RealVectorField drift(RealVectorField m) {
		RealScalarField scale = _model.getMs().pow(-1).times(
				-_model.getAlpha() * _model.getGamma() * 2 * Constants.KB * _temperature
						/ (Constants.MU0 * _topology.getCellVolume()));
		return m.times(scale);
	}
}
