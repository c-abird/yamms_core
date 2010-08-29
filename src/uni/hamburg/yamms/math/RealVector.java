package uni.hamburg.yamms.math;

/**
 * Represents a single real vector
 * 
 * @author Claas Abert
 * 
 */
public class RealVector {
	/** The components of the vector */
	private double[] _values;

	/**
	 * Standard constructor
	 * 
	 * @param values
	 *            the components of the vector
	 */
	public RealVector(double[] values) {
		_values = values;
	}

	/**
	 * Returns a new vector pointing in the same direction but normalized to 1.0
	 * 
	 * @return the norm
	 */
	public RealVector norm() {
		return norm(1.0);
	}

	/**
	 * Returns a new vector pointing in the same direction but normalized to
	 * <code>newNorm</code>
	 * 
	 * @param newNorm
	 *            the new norm
	 * @return the normed vector
	 */
	public RealVector norm(double newNorm) {
		double norm = 0;
		for (double value : _values) {
			norm += Math.pow(value, 2);
		}
		norm = Math.sqrt(norm);

		double[] values = _values.clone();
		for (int i = 0; i < values.length; ++i) {
			values[i] = values[i] / norm * newNorm;
		}

		return new RealVector(values);
	}

	/**
	 * Returns a copy of the vector components
	 * 
	 * @return the values (components)
	 */
	public double[] getValues() {
		return _values.clone();
	}
}
