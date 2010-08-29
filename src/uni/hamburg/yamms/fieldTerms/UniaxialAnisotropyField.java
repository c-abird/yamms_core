package uni.hamburg.yamms.fieldTerms;

import uni.hamburg.yamms.math.RealScalarField;
import uni.hamburg.yamms.math.RealVector;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.physics.Constants;
import uni.hamburg.yamms.solver.State;

/**
 * Uniaxial Anisotropy field term of 4th order arbitrary anisotropy direction
 * 
 * @author Claas Abert
 * @author Theo Gerhardt
 * 
 */
public class UniaxialAnisotropyField implements FieldTerm {
	/** Cached factor for the 1st order term */
	private RealVectorField _k1Prime;
	/** Cached factor for the 4th order term */
	private RealVectorField _k2Prime;
	/** the normed anisotropy direction */
	private double[] _axis;

	/**
	 * Standard constructor
	 * 
	 * @param k1
	 *            K1
	 * @param k2
	 *            K2
	 * @param ms
	 *            the saturation magnetization field
	 * @param axis
	 *            the anisotropy direction as vector
	 */
	public UniaxialAnisotropyField(double k1, double k2, RealScalarField ms, double[] axis) {
		_axis = new RealVector(axis).norm().getValues();
		_k1Prime = ms.pow(-2).times(2 * k1 / Constants.MU0).times(_axis);
		_k2Prime = ms.pow(-4).times(4 * k2 / Constants.MU0).times(_axis);
	}

	/**
	 * Alternative constructor. The anisotropy direction defaults to the z axis
	 * 
	 * @param k1
	 *            K1
	 * @param k2
	 *            K2
	 * @param ms
	 *            the saturation magnetization field
	 */
	public UniaxialAnisotropyField(double k1, double k2, RealScalarField ms) {
		this(k1, k2, ms, new double[] { 0, 0, 1 });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hamburg.m3sc.fieldTerms.FieldTerm#calculateField(uni.hamburg.m3sc
	 * .math.RealVectorField, double)
	 */
	public RealVectorField calculateField(State state) {
		RealScalarField msDotAxis = state.getM().dot(_axis);
		return _k1Prime.times(msDotAxis).add(_k2Prime.times(msDotAxis.pow(3)));
	}
}
