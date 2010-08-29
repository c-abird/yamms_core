package uni.hamburg.yamms.model;

import uni.hamburg.yamms.fieldTerms.FieldTerm;
import uni.hamburg.yamms.math.RealScalarField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.profiling.Profiler;
import uni.hamburg.yamms.solver.State;

/**
 * The standard micromagnetic model. Represents the Landau Lifshitz Gilbert
 * equation without any current terms.
 * 
 * @author Claas Abert
 * 
 */
public class BasicModel extends Model {
	/** scalar field for the normalization of the damping */
	protected RealScalarField _dampingFactor;
	
	/**
	 * Standard constructor
	 * 
	 * @param gamma
	 *            the gyromagnetic ratio
	 * @param alpha
	 *            the damping constant
	 * @param ms
	 *            the saturation magnetization
	 * @param field
	 *            the effective field
	 */
	public BasicModel(double gamma, double alpha, RealScalarField ms, FieldTerm field) {
		super(gamma, alpha, ms, field);
		_dampingFactor = ms.pow(-1).times(-_gammaPrime * _alpha);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hamburg.m3sc.model.Model#calculateDerivative(uni.hamburg.m3sc.math.RealVectorField, double)
	 */
	public RealVectorField calculateDerivative(State state) {
		Profiler.getInstance().tic("Model");
		RealVectorField m = state.getM();
		
		RealVectorField heff = _field.calculateField(state);

		RealVectorField precession = m.cross(heff).times(-_gammaPrime);
		RealVectorField damping = m.cross(m.cross(heff)).times(_dampingFactor);
		
		Profiler.getInstance().toc("Model");
		return precession.add(damping);
	}
}
