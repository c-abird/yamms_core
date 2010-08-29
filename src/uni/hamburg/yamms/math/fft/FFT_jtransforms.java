package uni.hamburg.yamms.math.fft;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * jTransforms implementation of the FFT service.
 * 
 * @author Claas Abert
 * @author Gunnar Selke
 * 
 */
public class FFT_jtransforms extends FFT {
	/** Scratch buffer for each thread */
	private double _part[];
	/** jJransforms object for each thread */
	private DoubleFFT_1D _fft;

	/**
	 * Constructor.
	 * 
	 * @param spec
	 *            the transform specifications
	 */
	public FFT_jtransforms(Spec spec) {
		super(spec);

		int n = spec.getTransformDimensions()[0].n;
		_part = new double[2 * n];
		_fft = new DoubleFFT_1D(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uni.hamburg.m3sc.math.fft.FFT#transform(double[], double[])
	 */
	public void transform(double[] in, double[] out) {
		if (_spec.getTransformDimensions()[0].n == 1) return;
		if (in != out) out = in.clone();
		loop(out);
	}

	/**
	 * Executes one iterated Fourier transform.
	 * 
	 * @param data
	 * @param offset
	 * @param thread_idx
	 */
	private void fft(double[] data, int offset) {
		assert _spec.getTransformRank() <= 1 : "not supported";
		if (_spec.getTransformRank() == 0) return;

		int n = _spec.getTransformDimensions()[0].n;
		if (n == 1) return;
		int stride = _spec.getTransformDimensions()[0].is;

		if (stride == 1) {
			switch (_spec.getType()) {
			case FORW_C2C:
				_fft.complexForward(data, 2 * offset);
				break;
			case BACK_C2C:
				_fft.complexInverse(data, 2 * offset, false);
				break;
			default:
				assert false;
			}
			return;
		}

		for (int i = 0; i < n; ++i) {
			_part[2 * i] = data[2 * (offset + i * stride)];
			_part[2 * i + 1] = data[2 * (offset + i * stride) + 1];
		}

		switch (_spec.getType()) {
		case FORW_C2C:
			_fft.complexForward(_part);
			break;
		case BACK_C2C:
			_fft.complexInverse(_part, 0, false);
			break;
		default:
			assert false;
		}

		for (int i = 0; i < n; ++i) {
			data[2 * (offset + i * stride)] = _part[2 * i];
			data[2 * (offset + i * stride) + 1] = _part[2 * i + 1];
		}
	}

	/**
	 * Executes the iterated Fourier transforms in a sequential or parallel
	 * loop.
	 * 
	 * @param data
	 *            input/output data
	 * @param parallel
	 *            parallel yes/no
	 */
	private void loop(double[] data) {
		if (_spec.getLoopRank() == 0) {
			fft(data, 0);
		} else {
			loop(data, 0, 0);
		}
	}

	/**
	 * Recursive loop function. The recursion is carried out over the loop
	 * dimensions
	 * 
	 * @param data
	 * @param dimIndex
	 * @param offset
	 * @param thread_idx
	 */
	private void loop(double[] data, int dimIndex, int offset) {
		final Dimension dim = _spec.getLoopDimensions()[dimIndex];
		for (int i = 0; i < dim.n; ++i) {
			int offs = offset + i * dim.is;
			if (dimIndex < _spec.getLoopRank() - 1) {
				loop(data, dimIndex + 1, offs);
			} else {
				fft(data, offs);
			}
		}
	}

}
