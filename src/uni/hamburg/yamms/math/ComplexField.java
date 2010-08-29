package uni.hamburg.yamms.math;

/**
 * N-dimensional field in the complex space
 * 
 * @author Claas Abert
 * 
 */
abstract public class ComplexField extends Field {

	/**
	 * Standard constructor
	 * 
	 * @param topology
	 *            the topology the field is defined on
	 * @param values
	 *            the values of the field. the real and imaginary parts are
	 *            stored blockwise (as direct neighbors)
	 */
	public ComplexField(Topology topology, double[][] values) {
		super(topology, values);

		// check precondition (super must be the first call)
		assert topology.totalCellCount * 2 == values[0].length;
	}

	/**
	 * Returns the real part of a component in a cell
	 * 
	 * @param lidx
	 *            the linear index of the cell
	 * @param component
	 *            the component
	 * @return the real part of the value
	 */
	public double getValueR(int lidx, int component) {
		return _values[component][lidx * 2];
	}

	/**
	 * Returns the imaginary part of a component in a cell
	 * 
	 * @param lidx
	 *            the linear index of the cell
	 * @param component
	 *            the component
	 * @return the imaginary part of the value
	 */
	public double getValueI(int lidx, int component) {
		return _values[component][lidx * 2 + 1];
	}

	/**
	 * Returns a FFTService object that fits the dimension of the topology (This
	 * does only work for three dimensional topologies and threedimensional
	 * topologies, that are stored as three dimensional topologies with a single
	 * cell in the third dimension
	 * 
	 * @return the FFT service
	 */
	protected FFTService getFFTService() {
		if (topology.getCellCount(2) == 1)
			return new FFT2DService(topology);
		else
			return new FFT3DService(topology);
	}

	/**
	 * Performs an inline fast fourier transformation 
	 */
	public void doFftForward() {
		assert _locked == false;

		for (int i = 0; i < dimension; i++) {
			getFFTService().complexForward(_values[i]);
		}
	}

	/**
	 * Performs an inline inverse fast fourier transformation
	 */
	public void doFftInverse() {
		assert _locked == false;

		for (int i = 0; i < dimension; i++) {
			getFFTService().complexInverse(_values[i]);
		}
	}

	public String toString() {
		String result = "";
		for (int i = 0; i < topology.totalCellCount; i++) {
			for (int j = 0; j < dimension; j++) {
				result += "" + getValueR(i, j) + "," + getValueI(i, j) + " ; ";
			}
			result += "\n";
		}
		return result;
	}

}
