package uni.hamburg.yamms.math;

/**
 * Easy to use service for fast fourier transforms of linearly stored data
 * 
 * @author Claas Abert
 * 
 */
public interface FFTService {
	/**
	 * Performs an inplace fast fourier transform
	 * 
	 * @param values
	 *            the linearly stored matrix to be transformed
	 */
	public void complexForward(double[] values);

	/**
	 * Performs an inplace inverse fast fourier transform
	 * 
	 * @param values
	 *            the linearly stored matrix to be transformed
	 */
	public void complexInverse(double[] values);
}
