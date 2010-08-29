package uni.hamburg.yamms.solver;

import java.util.Random;

import uni.hamburg.yamms.math.RealVectorField;

public class HeunSolver extends StochasticSolver {
	/**
	 * The standard constructor
	 * 
	 * @param stepSize
	 *            the step size used for integration
	 * @param seed
	 *            the seed for the random generation of the fluctuating field
	 * @param temperature
	 *            the temperature in K
	 */
	public HeunSolver(double stepSize, long seed, double temperature) {
		super();
		_stepSize = stepSize;
		_temperature = temperature;
		_random = new Random(seed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hamburg.m3sc.integrator.StochasticIntegrator#step(uni.hamburg.m3sc
	 * .math.RealVectorField, double, int)
	 */
	protected RealVectorField step(RealVectorField m, double t) {
		RealVectorField dW = generateDW();

		// predictor
		RealVectorField dm1 = _model.calculateDerivative(_currentState.derive(m, null, t)).times(_stepSize);
		dm1 = dm1.add(wienerIncrement(m, dW));

		// corrector
		RealVectorField m1 = m.add(dm1);
		RealVectorField dm2 = _model.calculateDerivative(_currentState.derive(m1, null, t + _stepSize)).times(_stepSize);
		dm2 = dm2.add(wienerIncrement(m1, dW));

		// mean derivative
		RealVectorField dm = dm1.add(dm2).times(0.5);
		RealVectorField mDot = dm.times(1/_stepSize); // TODO lazy initialization?
		
		_currentState.step(m, mDot, t);
		callHandlers();

		return m.add(dm).normTo(_model.getMs());
	}
}
