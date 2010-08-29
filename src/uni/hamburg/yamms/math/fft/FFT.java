package uni.hamburg.yamms.math.fft;

/**
 * Service for performing Fourier transforms.
 * 
 * @author Gunnar Selke
 * 
 */
public abstract class FFT {
	/** the FFT specification */
	protected final Spec _spec;

	/**
	 * Constructor.
	 * 
	 * @param spec
	 *            the transform specifications
	 */
	public FFT(Spec spec) {
		this._spec = spec;
	}

	/**
	 * 
	 * @param in
	 * @param out
	 */
	abstract public void transform(double[] in, double[] out);
}
