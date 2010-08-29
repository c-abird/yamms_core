package uni.hamburg.yamms.math;

/**
 * Scalar field in the real space
 * 
 * @author Claas Abert
 * 
 */
public class RealScalarField extends Field {

	/**
	 * Static method that creates a uniform (spatially constant) field on a
	 * certain topology
	 * 
	 * @param t
	 *            the topology
	 * @param value
	 *            the value of the field
	 * @return the real scalar field
	 */
	static public RealScalarField getUniformField(Topology t, double value) {
		return new RealConstantScalarField(t, value);
	}

	/**
	 * Alternative constructor
	 * 
	 * @param t
	 *            the topology
	 * @param values
	 *            the values of the field
	 */
	public RealScalarField(Topology t, double[] values) {
		super(t, new double[][] { values });
	}

	/**
	 * Standard constructor
	 * 
	 * @param t
	 *            the topology
	 * @param values
	 *            the values of the field. the value array is expected to be the
	 *            first entity of the array argument. This cumbersome method is
	 *            a consequence of the parent class <code>Field</code>
	 */
	public RealScalarField(Topology t, double[][] values) {
		super(t, values);

		// check precondition (super must be the first call)
		assert values.length == 1;
		assert topology.totalCellCount == values[0].length;
	}

	/**
	 * Calculates the point wise reciprocal and return the resulting scalar
	 * field. <strong>0</strong> will be mapped to <strong>0</strong>!
	 * 
	 * @return the resulting field
	 * @deprecated
	 */
	public RealScalarField getReciprocal() {
		double[] values = new double[topology.totalCellCount];
		for (int i = 0; i < topology.totalCellCount; i++) {
			if (getValue(i) == 0) continue;
			values[i] = 1 / getValue(i);
		}
		return new RealScalarField(topology, values);
	}

	/**
	 * Return the value of the field in a cell addressed by a linear index
	 * 
	 * @param lidx
	 *            the linear index
	 * @return the value
	 */
	public double getValue(int lidx) {
		return _values[0][lidx];
	}

	/**
	 * Calculates the point wise power of the field to a certain exponent
	 * 
	 * @param exponent
	 *            the exponent
	 * @return the resulting field
	 */
	public RealScalarField pow(double exponent) {
		double[] values = new double[topology.totalCellCount];
		for (int i = 0; i < topology.totalCellCount; i++) {
			if (getValue(i) == 0) continue;
			values[i] = Math.pow(getValue(i), exponent);
		}
		return new RealScalarField(topology, values);
	}

	/**
	 * Calculates the point wise square root
	 * 
	 * @return the point wise square root
	 */
	public RealScalarField sqrt() {
		double[] values = new double[topology.totalCellCount];
		for (int i = 0; i < topology.totalCellCount; i++) {
			if (getValue(i) == 0) continue;
			values[i] = Math.sqrt(getValue(i));
		}
		return new RealScalarField(topology, values);
	}

	/**
	 * Calculates the product with a real value
	 * 
	 * @param factor
	 *            the real factor
	 * @return the resulting scalar field
	 */
	public RealScalarField times(double factor) {
		double[] values = new double[topology.totalCellCount];
		for (int i = 0; i < topology.totalCellCount; i++) {
			values[i] = factor * getValue(i);
		}
		return new RealScalarField(topology, values);
	}

	/**
	 * Calculates the product with a vector and returns the resulting vector
	 * field
	 * 
	 * @param vec
	 *            the vector
	 * @return the resulting field
	 */
	public RealVectorField times(double[] vec) {
		double[][] result = new double[vec.length][topology.totalCellCount];
		for (int dim = 0; dim < vec.length; dim++) {
			for (int i = 0; i < topology.totalCellCount; i++) {
				result[dim][i] = vec[dim] * getValue(i);
			}
		}
		return new RealVectorField(topology, result);
	}

	/**
	 * Calculates the point wise product with another real scalar field
	 * 
	 * @param sf
	 *            the scalar field the multiply
	 * @return the resulting field
	 */
	public RealScalarField times(RealScalarField sf) {
		double[] values = new double[topology.totalCellCount];
		for (int i = 0; i < topology.totalCellCount; i++) {
			values[i] = sf.getValue(i) * getValue(i);
		}
		return new RealScalarField(topology, values);
	}

	/**
	 * Calculates the product with a real vector field and returns the result
	 * 
	 * @param vf
	 *            the vector field
	 * @return the resulting vector field
	 */
	public RealVectorField times(RealVectorField vf) {
		assert topology.equals(vf.topology);

		double[][] result = new double[vf.dimension][topology.totalCellCount];
		for (int i = 0; i < vf.dimension; i++) {
			for (int j = 0; j < topology.totalCellCount; j++) {
				result[i][j] = vf.getValue(i, j) * getValue(j);
			}
		}
		return new RealVectorField(topology, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < topology.totalCellCount; i++) {
			result.append("" + getValue(i) + "\n");
		}
		return result.toString();
	}

	/**
	 * Calculates the point wise sum with another real scalar field
	 * 
	 * @param sf
	 *            a real scalar field
	 * @return the resulting field
	 */
	public RealScalarField add(RealScalarField sf) {
		assert topology.equals(sf.topology);
		double[] values = new double[topology.totalCellCount];
		for (int i = 0; i < topology.totalCellCount; i++) {
			values[i] = sf.getValue(i) + getValue(i);
		}
		return new RealScalarField(topology, values);
	}

	/**
	 * Calculates the average value of the field
	 * 
	 * @return the average
	 */
	public double getAverage() {
		double result = 0;
		for (int i = 0; i < topology.totalCellCount; i++) {
			result += getValue(i);
		}
		return result / topology.totalCellCount;
	}

}
