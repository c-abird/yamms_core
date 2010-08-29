package uni.hamburg.yamms.fieldTerms;

import uni.hamburg.yamms.math.ComplexTensorField;
import uni.hamburg.yamms.math.ComplexVectorField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;
import uni.hamburg.yamms.math.fft.Dimension;
import uni.hamburg.yamms.math.fft.FFT;
import uni.hamburg.yamms.math.fft.Factory;
import uni.hamburg.yamms.math.fft.Spec;
import uni.hamburg.yamms.math.fft.Type;
import uni.hamburg.yamms.profiling.Profiler;
import uni.hamburg.yamms.solver.State;

/**
 * Sophisticated implementation of the demagnetization field. Reduces the FFT
 * complexity to 7/12, by taking advantage of the zero padded input field
 * 
 * @author Claas Abert
 * @author Gunnar Selke
 * 
 */
public class DemagField implements FieldTerm {
	/** The fourier transformed demagnetization tensor N */
	protected ComplexTensorField _fN;

	/** the topology of the untransformed demagnetization tensor */
	protected Topology _topology;

	/** the FFT services for the forward transform */
	FFT[] ifft;

	/** the FFT services for the inverse transform */
	FFT[] fft;

	/**
	 * Standard constructor. Takes a topology to calculate the demag tensor.
	 * 
	 * @param topology
	 *            the topology
	 */
	public DemagField(Topology topology) {
		ComplexTensorField N = DemagTensorField.fromTopology(topology);
		N = optimizeTensorSize(N);
		_topology = N.topology;
		_fN = N.fftForward().times(-1.0 / _topology.totalCellCount);

		final Topology t = _topology; // convenience alias

		// setup forward fft specs with clever zero padding
		fft = new FFT[t.dimension];
		for (int i = 0; i < t.dimension; i++) {
			Dimension[] loop = new Dimension[t.dimension - 1];
			Dimension[] trans = new Dimension[1];

			int k = 0;
			for (int j = 0; j < t.dimension; j++) {
				if (i == j) {
					trans[0] = new Dimension(t.getCellCount(i), t.getStride(i), t.getStride(i));
				} else {
					int size = (i < j) ? topology.getCellCount(j) : t.getCellCount(j);
					loop[k] = new Dimension(size, t.getStride(j), t.getStride(j));
					++k;
				}
			}
			fft[i] = Factory.instance().create(new Spec(Type.FORW_C2C, trans, loop));
		}

		// setup inverse fft specs
		ifft = new FFT[t.dimension];
		for (int i = 0; i < t.dimension; i++) {
			Dimension[] loop = new Dimension[t.dimension - 1];
			Dimension[] trans = new Dimension[1];

			int ii = t.dimension - i - 1;

			int k = 0;
			for (int j = 0; j < t.dimension; j++) {

				if (ii == j) {
					trans[0] = new Dimension(t.getCellCount(ii), t.getStride(ii), t.getStride(ii));
				} else {
					int size = (j > ii) ? topology.getCellCount(j) : t.getCellCount(j);
					loop[k] = new Dimension(size, t.getStride(j), t.getStride(j));
					++k;
				}
			}
			ifft[i] = Factory.instance().create(new Spec(Type.BACK_C2C, trans, loop));
		}
	}

	/**
	 * Extends the size of the demag tensor by zero padding so the number of
	 * cells in each direction is divisible by 4. This way the FFT calculations
	 * perform significantly faster.
	 * 
	 * @param N
	 *            the tensor to optimize
	 * @return the optimized tensor
	 */
	protected ComplexTensorField optimizeTensorSize(ComplexTensorField N) {
		int[] cellCount = N.topology.getCellCount();
		for (int i = 0; i < cellCount.length; i++) {
			if (cellCount[i] == 1 || cellCount[i] % 4 == 0) continue;
			cellCount[i] += 4 - (cellCount[i] % 4);
		}

		return N.applyTopology(
				new Topology(cellCount, N.topology.getCellSize(), N.topology.getOrigin()))
				.cyclicShiftTo(new int[] { 0, 0, 0 });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hamburg.m3sc.fieldTerms.FieldTerm#calculateField(uni.hamburg.m3sc
	 * .math.RealVectorField, double)
	 */
	public RealVectorField calculateField(State state) {
		// FFT
		ComplexVectorField fM = state.getM().applyTopology(_topology).toComplexVectorField();

		Profiler.getInstance().tic("Model.DemagField.FFT");
		double data[][] = fM.getValues();
		for (int i = 0; i < fM.dimension; ++i) {
			for (int j = 0; j < fft.length; ++j) {
				fft[j].transform(data[i], data[i]);
			}
		}
		Profiler.getInstance().toc("Model.DemagField.FFT");

		// multiplication
		Profiler.getInstance().tic("Model.DemagField.Multiplication");
		ComplexVectorField result = _fN.times(fM);
		Profiler.getInstance().toc("Model.DemagField.Multiplication");

		// inverse FFT
		Profiler.getInstance().tic("Model.DemagField.iFFT");
		data = result.getValues();
		for (int i = 0; i < result.dimension; ++i) {
			for (int j = 0; j < ifft.length; ++j) {
				ifft[j].transform(data[i], data[i]);
			}
		}
		Profiler.getInstance().toc("Model.DemagField.iFFT");

		return result.toRealVectorField().applyTopology(state.getTopology());
	}
}
