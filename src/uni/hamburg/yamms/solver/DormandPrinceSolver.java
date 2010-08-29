package uni.hamburg.yamms.solver;

import java.util.HashMap;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.events.EventException;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math.ode.sampling.FixedStepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.ode.sampling.StepNormalizer;

import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.model.Model;
import uni.hamburg.yamms.solver.stepHandlers.StepHandler;

/**
 * Uses the Apache common maths implementation of the Dormand Prince 54
 * integrator (4th order, 5th order correction).
 * 
 * @author Claas Abert
 * 
 */
public class DormandPrinceSolver extends AdaptiveStepsizeSolver {
	/** the apache integrator object */
	private final DormandPrince54Integrator _integrator;
	/** the stop time (hack for the event handling) */
	private double _stopTime;
	/** another helper for the conditional stop hack */
	private double _eventTime;

	/**
	 * Standard constructor
	 * 
	 * @param minStep
	 *            minimum step size
	 * @param maxStep
	 *            maximum step size
	 * @param absoluteTolerance
	 *            absolute tolerance
	 * @param relativeTolerance
	 *            relative tolerance
	 */
	public DormandPrinceSolver(double minStep, double maxStep, double absoluteTolerance,
			double relativeTolerance) {
		_integrator = new DormandPrince54Integrator(minStep, maxStep, absoluteTolerance,
				relativeTolerance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hamburg.m3sc.integrator.Integrator#integrate(uni.hamburg.m3sc.model
	 * .Model, uni.hamburg.m3sc.math.RealVectorField, double)
	 */
	public RealVectorField integrate(final Model model, final RealVectorField m0) {
		_currentState = new State(new HashMap<String, Object>(_startParams));
		_currentState.init(model, m0); // Dormand Prince needs init (mDot is
										// calculated for the first call of the
										// handlers)
		initStepHandling();
		initStopHandling();

		// initialize ode
		FirstOrderDifferentialEquations ode = new FirstOrderDifferentialEquations() {
			public void computeDerivatives(double t, double[] values, double[] result)
					throws DerivativeException {
				RealVectorField m = new RealVectorField(m0.topology, values).normTo(model.getMs());
				RealVectorField mDot = model.calculateDerivative(_currentState.derive(m, null, t));

				// write dm to result
				mDot.getLinearValues(result);
			}

			public int getDimension() {
				return m0.dimension * m0.topology.totalCellCount;
			}
		};

		// integrate
		try {
			double[] result = new double[m0.dimension * m0.topology.totalCellCount];
			_integrator.integrate(ode, 0, m0.normTo(model.getMs()).getLinearValues(),
					Double.MAX_VALUE, result);
			return new RealVectorField(m0.topology, result).normTo(model.getMs());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void addInterpolatedHandler(final StepHandler handler, double interval) {
		final Solver solver = this;
		StepNormalizer stepHandler = new StepNormalizer(interval, new FixedStepHandler() {
			public void handleStep(double t, double[] m, double[] mDot, boolean isLast)
					throws DerivativeException {
				handler.handleStep(solver, _currentState.derive(m, mDot, t));
			}
		});
		_integrator.addStepHandler(stepHandler);
	}

	/**
	 * initialized the step handling
	 */
	protected void initStepHandling() {
		// step based event handling
		org.apache.commons.math.ode.sampling.StepHandler stepHandler = new org.apache.commons.math.ode.sampling.StepHandler() {

			public void handleStep(StepInterpolator s, boolean isLast) throws DerivativeException {
				_currentState.step(s.getInterpolatedState(), s.getInterpolatedDerivatives(), s
						.getCurrentTime());
				callHandlers();
			}

			public boolean requiresDenseOutput() {
				return false;
			}

			public void reset() {
			}
		};
		_integrator.addStepHandler(stepHandler);
		callHandlers(); // first call for t = 0
	}

	/**
	 * Initializes the stop event handling
	 */
	protected void initStopHandling() {
		_stopTime = Double.MAX_VALUE;
		_integrator.addEventHandler(new EventHandler() {
			public void resetState(double t, double[] m) throws EventException {
			}

			public double g(double t, double[] m) throws EventException {
				_eventTime = t;
				return _stopTime - t;
			}

			public int eventOccurred(double t, double[] m, boolean increasing)
					throws EventException {
				return STOP;
			}
		}, Double.MAX_VALUE, 0, Integer.MAX_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uni.hamburg.m3sc.integrator.Integrator#stop()
	 */
	public void stop() {
		_stopTime = _eventTime;
	}

}
