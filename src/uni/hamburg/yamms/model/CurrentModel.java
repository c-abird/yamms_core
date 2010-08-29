package uni.hamburg.yamms.model;

import uni.hamburg.yamms.fieldTerms.FieldTerm;
import uni.hamburg.yamms.math.RealScalarField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.physics.Constants;
import uni.hamburg.yamms.solver.State;

/**
 * The micromagnetic model with the current terms introduced by Zang and Li.
 * 
 * @author Claas Abert
 * 
 */
public class CurrentModel extends Model {

	/** coupling constant b_j */
	private RealScalarField _bj;
	/** the current */
	private Current _current;
	/** the coupling constant b_j' */
	private RealScalarField _bjPrime;

	// Cached factors for calculateDerivative
	/** damping factor */
	private RealScalarField _dampingFactor;
	/** motion factor */
	private RealScalarField _motionFactor;
	/** distortion factot */
	private RealScalarField _distortionFactor;

	/**
	 * The standard constructor
	 * 
	 * @param gamma
	 *            the gyromagnetic ratio
	 * @param alpha
	 *            the damping constant
	 * @param ms
	 *            the saturation magnetization
	 * @param xi
	 *            the degree of nonadiabacity
	 * @param current
	 *            the current
	 * @param field
	 *            the effective field
	 */
	public CurrentModel(double gamma, double alpha, RealScalarField ms, double xi, Current current,
			FieldTerm field) {
		super(gamma, alpha, ms, field);
		_current = current;
		_bj = ms.pow(-1).times(Constants.MU_BOHR / (Constants.E_CHARGE * (1 + Math.pow(xi, 2))));
		_bjPrime = _bj.times(1.0 / (1.0 + Math.pow(_alpha, 2)));

		_dampingFactor = ms.pow(-1).times(-_gammaPrime * alpha);
		_motionFactor = _bjPrime.times(ms.pow(-2)).times(-(1 + alpha * xi));
		_distortionFactor = _bjPrime.times(ms.pow(-1)).times(-(xi - alpha));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hamburg.m3sc.model.Model#calculateDerivative(uni.hamburg.m3sc.math
	 * .RealVectorField, double)
	 */
	public RealVectorField calculateDerivative(State state) {
		RealVectorField m = state.getM();
		
		// llg terms
		RealVectorField heff = _field.calculateField(state);
		RealVectorField precession = m.cross(heff).times(-_gammaPrime);
		RealVectorField damping = m.cross(m.cross(heff)).times(_dampingFactor);

		// current terms
		RealVectorField current = _current.getCurrent(state.getTime());

		// TODO put in a method
		RealVectorField temp = RealVectorField.getEmptyField(m.topology, m.dimension);
		for (int i = 0; i < m.dimension; ++i) {
			temp = temp.add(current.getComponentScalarField(i).times(m.firstDerivative(i)));
		}
		temp = m.cross(temp);

		RealVectorField motion = m.cross(temp).times(_motionFactor);
		RealVectorField distortion = temp.times(_distortionFactor);

		return precession.add(damping).add(motion).add(distortion);
	}
}
