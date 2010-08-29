package uni.hamburg.yamms.fieldTerms;

import uni.hamburg.yamms.math.BooleanField;
import uni.hamburg.yamms.math.RealScalarField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.physics.Constants;
import uni.hamburg.yamms.solver.State;

/**
 * Implementation of the exchange field term using a nearest neighbor method for
 * the second derivative.
 * 
 * @author Claas Abert
 * 
 */
public class ExchangeField implements FieldTerm {
	/** constant factor of the exchange field term */
	protected RealScalarField _factor;
	/** The bounds of the sample used as an integer field (1 = material, 0 = vaccum) */
	protected BooleanField _bounds;

	/**
	 * Standard constructor
	 * 
	 * @param A
	 *            the exchange constant A
	 * @param ms
	 *            the saturation magnetization M_s as a scalar field
	 */
	public ExchangeField(double A, RealScalarField ms) {
		_bounds = BooleanField.boundsFromField(ms);
		_factor = ms.pow(-2).times(2 * A / Constants.MU0);
	}

	/* (non-Javadoc)
	 * @see uni.hamburg.m3sc.fieldTerms.FieldTerm#calculateField(uni.hamburg.m3sc.math.RealVectorField, double)
	 */
	public RealVectorField calculateField(State state) {
		return state.getM().laplaceWithBounds(_bounds).times(_factor);
	}

}
