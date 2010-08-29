package uni.hamburg.yamms.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.model.Model;
import uni.hamburg.yamms.solver.stepHandlers.StepHandler;

/**
 * Integrator to solve the ODE of the micromagnetic problem
 * 
 * @author Claas Abert
 * 
 */
public abstract class Solver {
	/**
	 * A step handler entry. Tuple of handler and interval in which it should be
	 * called (in terms of integration steps
	 */
	protected static class HandlerEntry {
		StepHandler h;
		Condition c;
	}

	/** The step handlers */
	protected ArrayList<HandlerEntry> _eventHandlers;
	/** the current simulation state */
	protected State _currentState;
	/** the initial values of the additional state parameters */
	protected HashMap<String, Object> _startParams;

	/**
	 * Standard constructor
	 */
	public Solver() {
		_eventHandlers = new ArrayList<HandlerEntry>();
		_startParams = new HashMap<String, Object>();
	}

	/**
	 * Integrates the ODE. Not reentrant!
	 * 
	 * @param model
	 *            the ODE
	 * @param m0
	 *            the initial magnetization
	 * @return the resulting magnetization
	 */
	public abstract RealVectorField integrate(final Model model, final RealVectorField m0);

	/**
	 * Add a handler that is called when the condition evaluates to true.
	 * 
	 * @param handler
	 *            the handler
	 * @param condition
	 *            the condition under which the handler is called
	 */
	public void addHandler(final StepHandler handler, final Condition condition) {
		_eventHandlers.add(new HandlerEntry() {
			{
				this.h = handler;
				this.c = condition;
			}
		});
	}

	/**
	 * Add a handler that is called at every step
	 * 
	 * @param handler
	 *            the handler
	 */
	public void addHandler(StepHandler handler) {
		addHandler(handler, Condition.always());
	}

	/**
	 * Add a handler that is called every nth step
	 * 
	 * @param handler
	 *            the handler
	 * @param interval
	 *            the interval in terms of steps
	 */
	public void addHandler(StepHandler handler, int interval) {
		addHandler(handler, Condition.everyNthStep(interval));
	}

	/**
	 * Add a handler that is called when the condition is true. Check the
	 * condition every nth step (performance)
	 * 
	 * @param handler
	 *            the handler
	 * @param condition
	 *            the condition
	 * @param interval
	 *            the interval the condition is checked in terms of steps
	 */
	public void addHandler(StepHandler handler, Condition condition, int interval) {
		addHandler(handler, Condition.everyNthStep(interval).and(condition));
	}

	/**
	 * Calls all handlers with the current <code>State</code> if the call
	 * conditions are fullfilled.
	 */
	protected void callHandlers() {
		Iterator<HandlerEntry> it = _eventHandlers.iterator();
		while (it.hasNext()) {
			HandlerEntry entry = it.next();
			if (entry.c.execute(_currentState)) entry.h.handleStep(this, _currentState);
		}
	}

	/**
	 * stops the integration process
	 */
	abstract public void stop();

	/**
	 * Convenience wrapper for stop handler
	 * 
	 * @param condition
	 *            the stop condition
	 */
	public void stopWhen(final Condition condition) {
		addHandler(new StepHandler() {
			public void handleStep(Solver solver, State state) {
				if (condition.execute(state)) stop();
			}
		}, condition);
	}

	/**
	 * Defines the start value of an additional state variable. (Takes only
	 * strings as values)
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setStartParamString(String key, String value) {
		_startParams.put(key, value);
	}

	/**
	 * Defines the start value of an additional state variable. (Takes only
	 * integers as values)
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setStartParamInt(String key, int value) {
		_startParams.put(key, value);
	}
}