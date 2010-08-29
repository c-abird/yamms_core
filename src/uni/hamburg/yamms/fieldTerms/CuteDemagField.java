package uni.hamburg.yamms.fieldTerms;

import uni.hamburg.yamms.math.ComplexTensorField;
import uni.hamburg.yamms.math.ComplexVectorField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;
import uni.hamburg.yamms.solver.State;

/**
 * A simple implementation of the demagnetization field, using the fast fourier
 * transform.
 * 
 * @author Claas Abert
 * 
 */
public class CuteDemagField implements FieldTerm {
	/** The fourier transformed demagnetization tensor N */
	protected ComplexTensorField _fN;

	/** the topology of the untransformed demagnetization tensor */
	protected Topology _topology;

	/**
	 * Standard constructor. Takes a topology to calculate the demag tensor.
	 * 
	 * @param topology
	 *            the topology
	 */
	public CuteDemagField(Topology topology) {
		_fN = DemagTensorField.fromTopology(topology).cyclicShiftTo(
				new int[] { 0, 0, 0 });
		_topology = _fN.topology;
		_fN = _fN.fftForward().times(-1);
	}

	/* (non-Javadoc)
	 * @see uni.hamburg.m3sc.fieldTerms.FieldTerm#calculateField(uni.hamburg.m3sc.math.RealVectorField, double)
	 */
	public RealVectorField calculateField(State state) {
		// FFT
		ComplexVectorField fM = state.getM().applyTopology(_topology)
				.toComplexVectorField().fftForward();

		// tensor multiplication and inversion FFT
		ComplexVectorField result = _fN.times(fM).fftInverse();

		return result.toRealVectorField().applyTopology(state.getTopology());
	}
}
