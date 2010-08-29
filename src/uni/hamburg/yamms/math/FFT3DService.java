package uni.hamburg.yamms.math;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_3D;

/**
 * Implements <code>FFTServcie</code> for three dimensional matrices
 * 
 * @author Claas Abert
 * 
 */
public class FFT3DService implements FFTService {
	/** The JTransforms FFT Service **/
	protected DoubleFFT_3D _service;

	/**
	 * Standard constructor. Takes the topology of the data to be transformed to
	 * receive the strides.
	 * 
	 * @param topology
	 *            the topology
	 */
	public FFT3DService(Topology topology) {
		_service = new DoubleFFT_3D(topology.getCellCount(2), topology
				.getCellCount(1), topology.getCellCount(0));
	}

	public void complexForward(double[] values) {
		_service.complexForward(values);
	}

	public void complexInverse(double[] values) {
		_service.complexInverse(values, true);

	}
}
