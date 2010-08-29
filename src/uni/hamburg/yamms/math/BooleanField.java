package uni.hamburg.yamms.math;

/**
 * A boolean field. Can be used to assign flags to the cells of a topology.
 * 
 * @author Claas Abert
 * 
 */
public class BooleanField {
	/** the boolean values */
	protected boolean[] _values;
	/** the topology of the field */
	public final Topology topology;

	/**
	 * Standard constructor
	 * 
	 * @param t
	 *            the topology
	 * @param values
	 *            the values of the field
	 */
	public BooleanField(Topology t, boolean[] values) {
		topology = t;
		_values = values;
	}

	/**
	 * Returns a <code>BooleanField</code> with <code>false</code> where the
	 * field values are 0 with a certain tolerance and <code>true</code>
	 * otherwise.
	 * 
	 * @param field
	 *            the field whose boundaries are determined
	 * @param tolerance
	 *            the boundary tolerance
	 * @return the boolean field
	 */
	static public BooleanField boundsFromField(RealScalarField field,
			double tolerance) {
		boolean[] values = new boolean[field.topology.totalCellCount];
		for (int i = 0; i < field.topology.totalCellCount; ++i) {
			values[i] = (Math.abs(field.getValue(i)) > tolerance);
		}
		return new BooleanField(field.topology, values);
	}

	/**
	 * Returns a <code>BooleanField</code> with <code>false</code> where the
	 * field values are 0 with a tolerance of 1e-6 and <code>true</code>
	 * otherwise.
	 * 
	 * @param field
	 *            the field whose boundaries are determined
	 * @return the boolean field
	 */
	static public BooleanField boundsFromField(RealScalarField field) {
		return boundsFromField(field, 1e-6);
	}

	/**
	 * Returns a <code>BooleanField</code> with <code>false</code> where the
	 * field values are 0 with a certain tolerance and <code>true</code>
	 * otherwise.
	 * 
	 * @param field
	 *            the field, whose boundaries are determined
	 * @param tolerance
	 *            the boundary tolerance
	 * @return the boolean field
	 */
	static public BooleanField boundsFromField(RealConstantScalarField field,
			double tolerance) {
		return new BooleanConstantField(field.topology, (Math.abs(field
				.getValue(0)) > tolerance));
	}

	/**
	 * Returns a <code>BooleanField</code> with <code>false</code> where the
	 * field values are 0 with a tolerance of 1e-6 and <code>true</code>
	 * otherwise.
	 * 
	 * @param field
	 *            the field whose boundaries are determined
	 * @return the boolean field
	 */
	static public BooleanField boundsFromFIeld(RealConstantScalarField field) {
		return boundsFromField(field, 1e-6);
	}

	/**
	 * The value at a certain linear index
	 * 
	 * @param lidx
	 *            the linear index
	 * @return the value
	 */
	public boolean getValue(int lidx) {
		return _values[lidx];
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < topology.totalCellCount; ++i) {
			result.append((getValue(i)) ? "true, " : "false, ");
		}
		return result.toString();
	}

}
