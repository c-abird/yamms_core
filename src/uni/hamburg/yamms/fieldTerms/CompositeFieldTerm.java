package uni.hamburg.yamms.fieldTerms;

import java.util.ArrayList;
import java.util.Iterator;

import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.profiling.Profiler;
import uni.hamburg.yamms.solver.State;

/**
 * Field term for combining multiple 'real' field terms by addition
 * 
 * @author Claas Abert
 * 
 */
public class CompositeFieldTerm implements FieldTerm {
	/** List of field terms that make up the combined field **/
	private ArrayList<FieldTerm> fieldTerms;

	/**
	 * Standard constructor
	 */
	public CompositeFieldTerm() {
		fieldTerms = new ArrayList<FieldTerm>();
	}

	/**
	 * Add a field term to the list
	 * 
	 * @param fieldTerm
	 *            the field term
	 */
	public void addFieldTerm(FieldTerm fieldTerm) {
		fieldTerms.add(fieldTerm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hamburg.m3sc.fieldTerms.FieldTerm#calculateField(uni.hamburg.m3sc
	 * .math.RealVectorField, double, int)
	 */
	public RealVectorField calculateField(State state) {
		Iterator<FieldTerm> it = fieldTerms.iterator();
		RealVectorField result = RealVectorField.getEmptyField(state.getTopology(),
				state.getM().dimension);

		while (it.hasNext()) {
			FieldTerm field = it.next();
			String profileKey = "Model." + Profiler.getSimpleClassName(field);
			Profiler.getInstance().tic(profileKey);

			result = result.add(field.calculateField(state));

			Profiler.getInstance().toc(profileKey);
		}

		return result;
	}
}
