package uni.hamburg.yamms.math;

/**
 * N-dimensional field on a topology. The values are expected to be saved in the
 * two dimensional array <code>_values</code>. The first index addresses the
 * dimension (component) of the field. The second index is a column major linear
 * index, that addresses the cell.
 * 
 * @author Claas Abert
 */
public abstract class Field {
	/** the topology the field is defined on */
	public final Topology topology;

	/** the values */
	protected final double[][] _values;

	/**
	 * the locked state (should be asserted to be <code>false</code> by state
	 * changing methods)
	 */
	protected boolean _locked;

	/** the dimension of the field (the number of components) */
	public final int dimension;

	/**
	 * alternative constructor with a one dimensional format of the values array
	 * 
	 * @param t
	 *            the topology the field is defined on
	 * @param values
	 *            like in the standard constructor, but with the component also
	 *            inlined with the highest stride
	 */
	public Field(Topology t, double[] values) {
		// TODO lazy initialization with on getValue ???
		int dim = values.length / t.totalCellCount;

		// create multidimensional array from values
		double[][] dvalues = new double[dim][t.totalCellCount];
		for (int i = 0; i < dim; i++) {
			System.arraycopy(values, i * t.totalCellCount, dvalues[i], 0,
					t.totalCellCount);
		}

		topology = t;
		_values = dvalues;
		_locked = false;
		dimension = dim;
	}

	/**
	 * standard constructor
	 * 
	 * @param t
	 *            the topology the field is defined on
	 * @param values
	 *            the values of the field, the first index addresses the
	 *            component of the field, the second index addresses the
	 *            discretized position in column major order (1st component has
	 *            stride 1)
	 */
	public Field(Topology t, double[][] values) {
		topology = t;
		_values = values;
		_locked = false;
		dimension = values.length;
	}

	/**
	 * Compares the topology and the values of the field with another field. The
	 * comparison of the values is performed with a given accuracy
	 * 
	 * @param o
	 *            the field to compare with
	 * @param accuracy
	 *            the accuracy
	 * @return true, if the fields are similar regarding the accuracy
	 */
	public boolean approx(Object o, double accuracy) {
		if (!(o instanceof Field))
			return false;
		Field vf = (Field) o;
		if (!topology.equals(vf.topology))
			return false;
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < topology.totalCellCount; j++) {
				if (Math.abs(_values[i][j] - vf.getValue(i, j)) > accuracy)
					return false;
			}
		}
		return true;
	}

	/**
	 * Copies the values of the field in a target array
	 * 
	 * @param target
	 *            the target array
	 */
	public void copyValuesTo(double[][] target) {
		for (int i = 0; i < _values.length; i++) {
			System.arraycopy(_values[i], 0, target[i], 0, target[i].length);
		}
	}

	@Override
	public boolean equals(Object o) {
		return approx(o, 0);
	}

	/**
	 * Returns a copy of the linearized values of a single component of the
	 * field
	 * 
	 * @param component
	 *            the component
	 * @return the values
	 */
	public double[] getComponent(int component) {
		double[] result = new double[_values[component].length];
		System.arraycopy(_values[component], 0, result, 0,
				_values[component].length);
		return result;
	}

	/**
	 * Returns a copy of the values of the field, whereas the component is
	 * inlined
	 * 
	 * @return the values
	 */
	public double[] getLinearValues() {
		double[] result = new double[dimension * _values[0].length];
		getLinearValues(result);
		return result;
	}

	/**
	 * Copies the values of the field in the values parameter, whereas the
	 * component is inlined
	 * 
	 * @param values
	 *            the value array to be filled
	 */
	public void getLinearValues(double[] values) {
		for (int i = 0; i < dimension; i++) {
			System.arraycopy(_values[i], 0, values, i * _values[i].length,
					_values[i].length);
		}
	}

	/**
	 * Returns the component of the field at a position defined by a linear
	 * index
	 * 
	 * @param component
	 *            the component
	 * @param lidx
	 *            the linear index
	 * @return the value
	 */
	public double getValue(int component, int lidx) {
		return _values[component][lidx];
	}

	/**
	 * Returns the component of the field at a position defined by a component
	 * index
	 * 
	 * @param component
	 *            the component
	 * @param cidx
	 *            the component index
	 * @return the value
	 */
	public double getValue(int component, int[] cidx) {
		return getValue(component, topology.getLinearIdx(cidx));
	}

	/**
	 * Returns a reference to the values without copying it. This method throws
	 * an assert exception if the locked state is set
	 * 
	 * @return the values
	 */
	public double[][] getValues() {
		assert _locked == false;
		return this._values;
	}

	/**
	 * Returns a copy of the values of the field
	 * 
	 * @return the values
	 */
	protected double[][] getValuesCopy() {
		double[][] result = new double[_values.length][];
		for (int i = 0; i < _values.length; i++) {
			result[i] = new double[_values[i].length];
			System.arraycopy(_values[i], 0, result[i], 0, result[i].length);
		}
		return result;
	}

	/**
	 * Returns the locked state of the field
	 * 
	 * @return the locked state
	 */
	public boolean isLocked() {
		return _locked;
	}

	/**
	 * Sets the locked state of the field. All methods, that change the state of
	 * the field should assert that the locked state is not set
	 */
	public void lock() {
		_locked = true;
	}
}
