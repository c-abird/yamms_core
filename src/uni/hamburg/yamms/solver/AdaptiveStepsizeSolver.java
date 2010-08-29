package uni.hamburg.yamms.solver;

import uni.hamburg.yamms.solver.stepHandlers.StepHandler;

/**
 * Adaptive stepsize integrator. Defines a step handling mechanism that is based
 * on a time interval in which the handler is called.
 * 
 * @author Claas Abert
 * 
 */
public abstract class AdaptiveStepsizeSolver extends Solver {
	/**
	 * Adds a step handler to the integrator that is called every
	 * <code>interval</code> time (the integration time). The state passed to
	 * the handler is interpolated if necessary.
	 * 
	 * @param handler
	 *            the step handler
	 * @param interval
	 *            the call interval
	 */
	public abstract void addInterpolatedHandler(final StepHandler handler, double interval);
}
