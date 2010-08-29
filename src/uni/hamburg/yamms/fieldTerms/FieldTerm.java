package uni.hamburg.yamms.fieldTerms;

import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.solver.State;

/**
 * A field term (effective field) for the solution of the LLG equation
 * 
 * @author Claas Abert
 * 
 */
public interface FieldTerm {
	/**
	 * Calculates the effective field for a given state
	 * 
	 * @param state the current state
	 */
	public RealVectorField calculateField(State state);

}
