package uni.hamburg.yamms.math;

/**
 * A spatially constant boolean field
 * 
 * @author Claas Abert
 * 
 */
public class BooleanConstantField extends BooleanField {
	/**
	 * Standard constructor
	 * 
	 * @param t the topology
	 * @param value the value of the field everywhere
	 */
	public BooleanConstantField(Topology t, boolean value) {
		super(t, new boolean[] { value });
	}
	
	/* (non-Javadoc)
	 * @see uni.hamburg.m3sc.math.BooleanField#getValue(int)
	 */
	public boolean getValue(int lidx) {
		return _values[0];
	}

}
