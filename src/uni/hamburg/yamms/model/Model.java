package uni.hamburg.yamms.model;

import uni.hamburg.yamms.fieldTerms.FieldTerm;
import uni.hamburg.yamms.math.RealScalarField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.solver.State;

/**
 * The micromagnetic model used. Represents the differential equation of motion
 * by providing a method the calculates the derivative of the magnetization.
 * <p>
 * The most common implementation will calculate the right side of the Landau
 * Lifshitz equation.
 * 
 * @author Claas Abert
 * 
 */
public abstract class Model {
	/** the gyromagnetic ratio gamma */
	protected final double _gamma;
	/** the gyromagnetic ratio gamma' */
	protected final double _gammaPrime;
	/** the damping constant alpha */
	protected final double _alpha;
	/** the effective field */
	protected final FieldTerm _field;
	/** the saturation magnetization */
	protected final RealScalarField _ms;

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
	public Model(double gamma, double alpha, RealScalarField ms, FieldTerm field) {
		_gamma = gamma;
		_gammaPrime = gamma / (1.0 + Math.pow(alpha, 2));
		_alpha = alpha;
		_ms = ms;
		_field = field;
	}

	/**
	 * Calculates the derivative of the ODE of motion
	 * 
	 * @param state
	 *            the current state of the integration
	 * @return the derivative
	 */
	public abstract RealVectorField calculateDerivative(State state);

	/**
	 * Returns the saturation magnetization of the model
	 * 
	 * @return the saturation magnetization
	 */
	public RealScalarField getMs() {
		return _ms;
	}

	/**
	 * Returns the damping constant alpha
	 * 
	 * @return alpha
	 */
	public double getAlpha() {
		return _alpha;
	}

	/**
	 * Returns the gyromagnetic ratio gamma
	 * 
	 * @return gamma
	 */
	public double getGamma() {
		return _gamma;
	}

	/**
	 * Returns the gyromagnetic ratio gamma'
	 * 
	 * @return gamma'
	 */
	public double getGammaPrime() {
		return _gammaPrime;
	}
}
