package uni.hamburg.yamms.model;

import uni.hamburg.yamms.math.RealVectorField;

/**
 * Represents a current through the sample
 * 
 * @author Claas Abert
 * 
 */
public interface Current {

	/**
	 * Returns the current at a specified time as a vector field
	 * 
	 * @param t
	 *            the time
	 * @return the current field
	 */
	public RealVectorField getCurrent(double t);

}
