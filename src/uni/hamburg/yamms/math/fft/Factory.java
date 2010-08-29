package uni.hamburg.yamms.math.fft;

public class Factory {
	static private enum Implementation {
		JTRANSFORMS, FFTW3,
	}

	private Implementation impl;

	/** the singleton instance */
	static private Factory the_instance;

	/**
	 * @return the singleton instance
	 */
	static public Factory instance() {
		if (the_instance == null)
			the_instance = new Factory();
		return the_instance;
	}

	/**
	 * Constructor.
	 */
	private Factory() {
		// Auto-selection FFT implementation
		impl = FFT_fftw3Impl.isAvailable() ? Implementation.FFTW3
				: Implementation.JTRANSFORMS;
		// impl = Implementation.JTRANSFORMS;
		// impl = Implementation.FFTW3;
	}

	/**
	 * Creates a new object implementing the FFT interface. Currently, the
	 * jTransforms and the FFTW3 libraries are supported.
	 * 
	 * @param spec
	 * @return the FFT service
	 */
	public FFT create(Spec spec) {
		switch (impl) {
		case FFTW3:
			return new FFT_fftw3Impl(spec, 4);
		case JTRANSFORMS:
			return new FFT_jtransforms(spec);
		default:
			assert false;
			return null;
		}
	}
}
