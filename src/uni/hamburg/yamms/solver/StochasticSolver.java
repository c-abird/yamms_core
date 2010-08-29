package uni.hamburg.yamms.solver;

import java.util.HashMap;
import java.util.Random;

import uni.hamburg.yamms.math.RealScalarField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;
import uni.hamburg.yamms.model.Model;
import uni.hamburg.yamms.physics.Constants;

/**
 * A stochastic integrator. The class provides methods needed for thermal
 * activated simulations. It calculated the derivation of the wiener process and
 * calculates the increment of the magnetization caused by the thermal
 * activation
 * 
 * @author Claas Abert
 * 
 */
abstract public class StochasticSolver extends Solver {

	/** the step size */
	protected double _stepSize;
	/** the temperature in K */
	protected double _temperature;
	/** the random generator */
	protected Random _random;
	/** the ode used */
	protected Model _model;
	/** the topology of the problem */
	protected Topology _topology;
	/** the dimension of the magnetization (usually 3) */
	protected int _dimension;
	/** continue flag */
	protected boolean _continue;

	// Cached values
	/** the deviation of the fluctuating field */
	protected RealScalarField _deviation;
	/** cached factor for wiener increment */
	protected RealScalarField _dampingFactor;

	/**
	 * Generates the random Wiener increment dW
	 * 
	 * @return the increment
	 */
	protected RealVectorField generateDW() {
		double[][] values = new double[_dimension][_topology.totalCellCount];
		for (int i = 0; i < _dimension; i++) {
			for (int j = 0; j < _topology.totalCellCount; j++) {
				values[i][j] = _random.nextGaussian() * _deviation.getValue(j);
			}
		}
		return new RealVectorField(_topology, values);
	}

	/**
	 * Calculates the deviation of the fluctuating field from the temperature,
	 * topology,...
	 * 
	 * @return the deviation
	 */
	protected RealScalarField getDeviation(Model model) {
		double var = model.getAlpha() / (1 + Math.pow(model.getAlpha(), 2)) * 2 * Constants.KB
				* _temperature / (model.getGamma() * Constants.MU0 * _topology.getCellVolume());
		return model.getMs().pow(-1).times(var).sqrt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hamburg.m3sc.integrator.Integrator#integrate(uni.hamburg.m3sc.model
	 * .Model, uni.hamburg.m3sc.math.RealVectorField, double)
	 */
	synchronized public RealVectorField integrate(Model model, RealVectorField m0) {
		_currentState = new State(new HashMap<String, Object>(_startParams));
		_model = model;
		_topology = m0.topology;
		_dimension = m0.dimension;
		_deviation = getDeviation(model).times(Math.sqrt(_stepSize));
		_dampingFactor = _model.getMs().pow(-1).times(-_model.getGammaPrime() * _model.getAlpha());

		// stop and stage handling
		_continue = true;

		double t = 0;
		RealVectorField m = m0;

		while (_continue) {
			m = step(m, t);
			t += _stepSize;
		}
		return m;
	}

	/**
	 * The step method used by the <code>integrate</code> method. Returns the
	 * next calculated magnetization for a given current magnetization
	 * 
	 * @param m
	 *            the current magnetization
	 * @param t
	 *            the current integration time
	 * @param step
	 *            the current step number
	 * @return the new magnetization
	 */
	abstract protected RealVectorField step(RealVectorField m, double t);

	/**
	 * Builds the increment of the magnetization that is caused by the
	 * temperature effects
	 * 
	 * @param M
	 *            the magnetization
	 * @param dW
	 *            the wiener increment
	 * @return the increment of the magentization
	 */
	protected RealVectorField wienerIncrement(RealVectorField M, RealVectorField dW) {
		RealVectorField precession = M.cross(dW).times(-_model.getGammaPrime());
		RealVectorField damping = M.cross(M.cross(dW)).times(_dampingFactor);

		return precession.add(damping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uni.hamburg.m3sc.integrator.Integrator#stop()
	 */
	public void stop() {
		_continue = false;
	}
}
