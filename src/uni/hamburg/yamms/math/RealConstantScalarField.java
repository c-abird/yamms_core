package uni.hamburg.yamms.math;

/**
 * Represents a spatially constant real scalar field.
 * 
 * @author Claas Abert
 * 
 */
public class RealConstantScalarField extends RealScalarField {
	/**
	 * Standard constructor
	 * 
	 * @param t
	 *            the topology the field is defined on
	 * @param value
	 *            the value of the field
	 */
	public RealConstantScalarField(Topology t, double value) {
		super(t, new double[] { value });
	}

	/* (non-Javadoc)
	 * @see uni.hamburg.m3sc.math.RealScalarField#getValue(int)
	 */
	public double getValue(int i) {
		return _values[0][0];
	}
}
