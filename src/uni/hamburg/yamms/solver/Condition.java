package uni.hamburg.yamms.solver;

import uni.hamburg.yamms.solver.State;

/**
 * Condition on simulation state for stop and stage handling during the
 * integration
 * 
 * @author Claas Abert
 * 
 */
public abstract class Condition {
	/**
	 * Checks the condition and returns the boolean result
	 * 
	 * @param state
	 *            the state
	 * @return the result
	 */
	abstract public boolean execute(State state);

	/**
	 * Returns a compound 'and' condition
	 * 
	 * @param condition
	 *            condition to be 'and'ed
	 * @return compound condition
	 */
	public Condition and(final Condition condition) {
		final Condition self = this;
		return new Condition() {

			@Override
			public boolean execute(State state) {
				return self.execute(state) && condition.execute(state);
			}
		};
	}

	/**
	 * Returns a compound 'or' condition
	 * 
	 * @param condition
	 *            condition to be 'or'ed
	 * @return compound condition
	 */
	public Condition or(final Condition condition) {
		final Condition self = this;
		return new Condition() {

			@Override
			public boolean execute(State state) {
				return self.execute(state) || condition.execute(state);
			}
		};
	}

	/**
	 * Returns a inverted (not) condition
	 * 
	 * @return inverted condition
	 */
	public Condition not() {
		final Condition self = this;
		return new Condition() {

			@Override
			public boolean execute(State state) {
				return !self.execute(state);
			}
		};
	}

	/**
	 * Returns a condition that is true for times greater than t.
	 * 
	 * @param t
	 *            the time
	 * @return the condition
	 */
	public static Condition timeGreater(final double t) {
		return new Condition() {
			@Override
			public boolean execute(State state) {
				return state.getTime() > t;
			}
		};
	}

	/**
	 * Returns a condition that is true for a maximum norm of MDot less than
	 * norm (relaxation condition).
	 * 
	 * @param norm
	 *            the norm
	 * @return the condition
	 */
	public static Condition mDotMaxNormLess(final double norm) {
		return new Condition() {
			@Override
			public boolean execute(State state) {
				return state.getMDot().getMaxNorm() < norm;
			}
		};
	}

	/**
	 * Returns a condition that is true for every nth step (modulo condition)
	 * 
	 * @param interval
	 *            the interval in steps
	 * @return the condition
	 */
	public static Condition everyNthStep(final int interval) {
		return new Condition() {
			@Override
			public boolean execute(State state) {
				return state.getStep() % interval == 0;
			}
		};
	}

	/**
	 * Condition that is always true
	 * 
	 * @return the condition
	 */
	public static Condition always() {
		return new Condition() {
			@Override
			public boolean execute(State state) {
				return true;
			}
		};
	}

}
