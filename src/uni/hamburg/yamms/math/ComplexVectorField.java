package uni.hamburg.yamms.math;

/**
 * N-dimensional vector field in the complex space
 * 
 * @author Claas Abert
 * 
 */
public class ComplexVectorField extends ComplexField {
	/**
	 * Standard constructor
	 * 
	 * @param topology
	 *            the topology
	 * @param values
	 *            the values of the field
	 * @see ComplexField
	 */
	public ComplexVectorField(Topology topology, double[][] values) {
		super(topology, values);
	}

	public ComplexVectorField clone() {
		return new ComplexVectorField(topology, getValuesCopy());
	}

	/**
	 * Calculates the fast fourier transform and returns the resulting field
	 * 
	 * @return the resulting field
	 */
	public ComplexVectorField fftForward() {
		ComplexVectorField result = clone();
		result.doFftForward();
		return result;
	}

	/**
	 * Calculates the inverse fast fourier transform and returns the resulting
	 * field
	 * 
	 * @return the resulting field
	 */
	public ComplexVectorField fftInverse() {
		ComplexVectorField result = clone();
		result.doFftInverse();
		return result;
	}

	/**
	 * Convert to a real vector field by omitting the complex parts
	 * 
	 * @return the real vector field
	 */
	public RealVectorField toRealVectorField() {
		double[][] result = new double[dimension][topology.totalCellCount];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < topology.totalCellCount; j++) {
				result[i][j] = _values[i][j * 2];
			}
		}
		return new RealVectorField(topology, result);
	}

}
