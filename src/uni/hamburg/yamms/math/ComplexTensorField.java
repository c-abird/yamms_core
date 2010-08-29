package uni.hamburg.yamms.math;

/**
 * 3x3 Complex tensor field
 * 
 * @author Claas Abert
 * 
 */
public class ComplexTensorField extends ComplexField {
	/**
	 * The mapping of the tensor components. The default mapping is
	 * <code>[0, 1, 2, 3, 4, 5, 6, 7, 8]</code>, meaning that the values with
	 * index 0 represent the <code>xx</code> component, the values with index 1
	 * <code>xy</code>...
	 * <p>
	 * A symmetric tensor field will have a mapping like this:
	 * <code>[0, 1, 2, 3, 2, 4, 5, 3, 5, 6[</code>.
	 */
	protected int[] _map;

	/**
	 * Alternative constructor. The mapping defaults to
	 * <code>[0, 1, 2, 3, 4, 5, 6, 7, 8]</code>
	 * 
	 * @param topology
	 *            the topology
	 * @param values
	 *            the values of the field
	 */
	public ComplexTensorField(Topology topology, double[][] values) {
		this(topology, values, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 });
	}

	/**
	 * Standard constructor. The <code>map</code> parameter defines the mapping
	 * of the index in the <code>values</code> array to the components of the
	 * tensor. <code>[0, 1, 2, 3, 4, 5, 6, 7, 8]</code> means that the values
	 * with index 0 represent the <code>xx</code> component, the values with
	 * index 1 <code>xy</code>... A symmetric tensor would have a mapping of
	 * <code>[0, 1, 2, 1, 3, 4, 2, 4, 5]</code>
	 * 
	 * @param topology
	 *            the topology
	 * @param values
	 *            the values of the field
	 * @param map
	 *            the mapping of the components
	 */
	// TODO allow arbitrary dimensions
	public ComplexTensorField(Topology topology, double[][] values, int[] map) {
		super(topology, values);
		_map = map;
	}

	public ComplexTensorField clone() {
		return new ComplexTensorField(topology, getValuesCopy(), _map);
	}

	/**
	 * Move the topology to a specified origin while shifting the values of the
	 * field cyclic
	 * 
	 * @param origin
	 *            the new origin
	 * @return the resulting field
	 */
	public ComplexTensorField cyclicShiftTo(int[] origin) {
		Topology t = topology.resetOrigin();

		double[][] result = new double[dimension][2 * topology.totalCellCount];

		int[] shift = new int[topology.dimension];
		for (int i = 0; i < topology.dimension; ++i) {
			shift[i] = topology.getOrigin(i) - origin[i];
		}

		for (int i = 0; i < dimension; ++i) {
			for (int j = 0; j < topology.totalCellCount; ++j) {
				int[] cidx = t.getCompIdx(j);
				for (int k = 0; k < cidx.length; ++k) {
					cidx[k] += shift[k];
					while (cidx[k] < 0)
						cidx[k] += t.getCellCount(k);
					cidx[k] %= t.getCellCount(k);
				}
				result[i][t.getLinearIdx(cidx) * 2] = _values[i][j * 2];
				result[i][t.getLinearIdx(cidx) * 2 + 1] = _values[i][j * 2 + 1];
			}
		}

		return new ComplexTensorField(topology.applyOrigin(origin), result, _map);
	}

	/**
	 * Calculates the fast fourier transform of the field
	 * 
	 * @return the resulting field
	 */
	public ComplexTensorField fftForward() {
		ComplexTensorField result = clone();
		result.doFftForward();
		return result;
	}

	/**
	 * Calculates the inverse fast fourier transform of the field
	 * 
	 * @return the resulting field
	 */
	public ComplexTensorField fftInverse() {
		ComplexTensorField result = clone();
		result.doFftInverse();
		return result;
	}

	/**
	 * Calculates the product with a complex vector field
	 * 
	 * @param vf
	 *            the complex vector field
	 * @return the resulting field
	 */
	public ComplexVectorField times(final ComplexVectorField vf) {
		assert vf.dimension == 3;
		assert topology.equals(vf.topology);

		final double[][] result = new double[3][topology.totalCellCount * 2];

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 3; k++) {
				int jk = _map[3 * j + k];
				for (int i = 0; i < topology.totalCellCount; i++) {
					result[j][i * 2] += getValueR(i, jk) * vf.getValueR(i, k)
					                  - getValueI(i, jk) * vf.getValueI(i, k);

					result[j][i * 2 + 1] += getValueR(i, jk) * vf.getValueI(i, k)
							              + getValueI(i, jk) * vf.getValueR(i, k);
				}
			}
		}
		return new ComplexVectorField(topology, result);
	}

	/**
	 * Calculates the product with a real scalar
	 * 
	 * @param fac
	 *            the scalar factor
	 * @return the resulting field
	 */
	public ComplexTensorField times(double fac) {
		double[][] result = new double[dimension][topology.totalCellCount * 2];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < topology.totalCellCount * 2; j++) {
				result[i][j] = fac * _values[i][j];
			}
		}
		return new ComplexTensorField(topology, result, _map);
	}

	/**
	 * Applies another topology to the vector field and return the resulting
	 * field. If the new topology is bigger than the current one, the field will
	 * be zero padded. if the new topology is smaller than the field will be
	 * cropped.
	 * 
	 * @param newTopology
	 *            the topology to be applied
	 * @return the resulting vector field
	 */
	public ComplexTensorField applyTopology(final Topology newTopology) {
		assert topology.dimension == newTopology.dimension;
		// TODO handle change of cell size

		final double[][] result = new double[dimension][newTopology.totalCellCount * 2];

		final int[] start = new int[topology.dimension];
		final int[] interval = new int[topology.dimension];
		for (int i = 0; i < topology.dimension; ++i) {
			start[i] = Math.max(newTopology.getOrigin(i), topology.getOrigin(i));
			interval[i] = Math.min(newTopology.getMaxIndex(i), topology.getMaxIndex(i)) - start[i];
			// TODO check if stop always bigger than start
		}
		for (int j = 0; j < dimension; j++) {
			// TODO deal with 1 dim topologies
			applyTopologyLoop(_values[j], result[j], newTopology, interval, 1, topology
					.getLinearIdx(start), newTopology.getLinearIdx(start));
		}
		return new ComplexTensorField(newTopology, result, _map);
	}

	/**
	 * Helper method for applyTopology for the recursive iteration over the
	 * linear index
	 * 
	 * @param source
	 *            the source values
	 * @param target
	 *            the target array
	 * @param t
	 *            the new topology to be applied
	 * @param interval
	 *            the intersection of the current and the new topology
	 * @param dimIndex
	 *            the dimension handled (recursion)
	 * @param sOffset
	 *            the origin of the source topology
	 * @param tOffset
	 *            the origin of the target topology
	 */
	private void applyTopologyLoop(double[] source, double[] target, Topology t, int[] interval,
			int dimIndex, int sOffset, int tOffset) {
		for (int i = 0; i < interval[dimIndex]; ++i) {
			int sOffs = sOffset + i * topology.getStride(dimIndex); // source
			// offset
			int tOffs = tOffset + i * t.getStride(dimIndex); // target offset
			if (dimIndex < topology.dimension - 1) {
				applyTopologyLoop(source, target, t, interval, dimIndex + 1, sOffs, tOffs);
			} else {
				System.arraycopy(source, sOffs * 2, target, tOffs * 2, interval[0] * 2);
			}
		}
	}
}
