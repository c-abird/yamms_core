package uni.hamburg.yamms.math;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

/**
 * Implements <code>FFTServcie</code> for two dimensional matrices
 * 
 * @author Claas Abert
 * 
 */
public class FFT2DService implements FFTService {
	/** The JTransforms FFT Service **/
	protected DoubleFFT_2D _service;

	/**
	 * Standard constructor. Takes the topology of the data to be transformed to
	 * receive the strides.
	 * 
	 * @param topology
	 *            the topology
	 */
	public FFT2DService(Topology topology) {
		_service = new DoubleFFT_2D(topology.getCellCount(1), topology
				.getCellCount(0));
	}

	public void complexForward(double[] values) {
		_service.complexForward(values);
	}

	public void complexInverse(double[] values) {
		_service.complexInverse(values, true);
	}
}
