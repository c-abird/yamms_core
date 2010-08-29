package uni.hamburg.yamms.solver.stepHandlers;

import uni.hamburg.yamms.solver.Solver;
import uni.hamburg.yamms.solver.State;

/**
 * A step handler called from a solver. The solver is supposed to call
 * the handler regularly with information of the current state of the
 * integration
 * 
 * @author Claas Abert
 * 
 */
public interface StepHandler {

	/**
	 * The step handling method that is called from the solver
	 * 
	 * @param solver
	 *            the instance of the solver
	 * @param state
	 *            the current state of the integration
	 */
	void handleStep(Solver solver, State state);

}
