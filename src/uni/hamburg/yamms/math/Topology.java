package uni.hamburg.yamms.math;

/**
 * Represents a n-dimensional topology. Topology means a discretized rectangular
 * area in the n-dimensional space. The area is characterized by the edge
 * lengths of its cells, the number of cells in the n dimensions and the origin
 * in terms of cells.
 * <p>
 * The topology offers a variety of methods for index conversions. There are
 * three types of indezes:
 * <ul>
 * <li><strong>linear index</strong> with column major order
 * <li><strong>component index</strong>: each cell is addressed with a tuple of
 * integers (coordinates). (0,0,0...) represents the origin. negative values are
 * possible
 * <li><strong>position</strong>: same as component index, but the tuple is
 * weighted with the edge lengths of the cells
 * </ul>
 * 
 * @author Claas Abert
 */
public class Topology {
	/**
	 * helper for the alternative constructors to convert the topology size to
	 * cell sizes
	 * 
	 * @param size
	 *            the edge lengths of the topology
	 * @param cellCount
	 *            the number of cells in each dimension
	 * @return the cell size array
	 */
	private static double[] size2cellSize(double[] size, int[] cellCount) {
		double[] result = new double[size.length];
		for (int i = 0; i < size.length; i++) {
			result[i] = size[i] / cellCount[i];
		}
		return result;
	}

	/** the edge lengths of a cell */
	protected final double[] _cellSize;

	/** the numbers of cells */
	protected final int[] _cellCount;

	/** the origin of the topology in terms of cells */
	protected final int[] _origin;

	/** the stride for linear storage of field values */
	protected final int[] _stride;

	/** the dimension of the topology */
	public final int dimension;

	/** the total number of cells */
	public final int totalCellCount;

	/** the squared edge length of a cell */
	public final double[] _squaredCellSize;

	/**
	 * the first index addresses the cells of the topology. the second index
	 * addresses the possible <code>dimension * 2</code> neighbor cells and
	 * contains the stride if the neighbor exists and 0 if not
	 */
	private int[][] _neighborStrides;

	/**
	 * alternative constructor, the total size of the topology is passed instead
	 * of the cell sizes, the origin is set to 0
	 * 
	 * @param size
	 *            the edge lengths of the topology
	 * @param cellCount
	 *            the number of cells in each dimension
	 */
	public Topology(double[] size, int[] cellCount) {
		this(cellCount, size2cellSize(size, cellCount));
	}

	/**
	 * alternative constructor, the total size of the topology is passed instead
	 * of the cell sizes
	 * 
	 * @param size
	 *            the edge lengths of the topology
	 * @param cellCount
	 *            the number of cells in each dimension
	 * @param origin
	 *            the origin of the topology in terms of cells
	 */
	public Topology(double[] size, int[] cellCount, int[] origin) {
		this(cellCount, size2cellSize(size, cellCount), origin);
	}

	/**
	 * alternative constructor, the origin is set to 0
	 * 
	 * @param cellCount
	 *            the number of cells in each dimension
	 * @param cellSize
	 *            the cell size in each dimension (edge length)
	 */
	public Topology(int[] cellCount, double[] cellSize) {
		this(cellCount, cellSize, new int[cellCount.length]);
	}

	/**
	 * standard constructor
	 * 
	 * @param cellCount
	 *            the number of cells in each dimension
	 * @param cellSize
	 *            the cell size in each dimension (edge length)
	 * @param origin
	 *            the origin of the topology in terms of cells
	 */
	public Topology(int[] cellCount, double[] cellSize, int[] origin) {
		_cellCount = cellCount;
		_cellSize = cellSize;
		_origin = origin;

		// calculate dimension
		dimension = cellCount.length;

		// calculate strides
		_stride = new int[dimension + 1];
		for (int i = 0; i < dimension + 1; i++) {
			if (i == 0) {
				_stride[i] = 1;
				continue;
			}
			_stride[i] = _cellCount[i - 1] * _stride[i - 1];
		}

		// calculate squared cell sizes
		_squaredCellSize = new double[dimension];
		for (int i = 0; i < dimension; ++i) {
			_squaredCellSize[i] = Math.pow(_cellSize[i], 2);
		}

		// calculate total cell count
		totalCellCount = _stride[dimension];

		_neighborStrides = null;
	}

	/**
	 * Returns a similar topology with a changed origin
	 * 
	 * @param origin
	 *            the origin
	 * @return the resulting topology
	 */
	public Topology applyOrigin(int[] origin) {
		return new Topology(_cellCount, _cellSize, origin);
	}

	public boolean equals(Object o) {
		if (!(o instanceof Topology)) return false;
		Topology t = (Topology) o;

		for (int i = 0; i < _cellCount.length; i++) {
			if (t.getCellCount(i) != _cellCount[i]) return false;
		}
		for (int i = 0; i < _cellSize.length; i++) {
			if (t.getCellSize(i) != _cellSize[i]) return false;
		}
		for (int i = 0; i < _origin.length; i++) {
			if (t.getOrigin(i) != _origin[i]) return false;
		}
		return true;
	}

	/**
	 * Returns the cell count array
	 * 
	 * @return all cell counts (number of cells) in an array
	 */
	public int[] getCellCount() {
		return _cellCount.clone();
	}

	/**
	 * Returns the number of cells in a direction
	 * 
	 * @param i
	 *            the index of the component (the direction)
	 * @return the number of cells
	 */
	public int getCellCount(int i) {
		return _cellCount[i];
	}

	/**
	 * Returns a copy of the cell sizes array (edge lengths in different
	 * directions)
	 * 
	 * @return the array of cell sizes
	 */
	public double[] getCellSize() {
		return _cellSize.clone();
	}

	/**
	 * Returns the cell size in a given direction (the edge length)
	 * 
	 * @param i
	 *            the index of the component (the direction)
	 * @return the edge length
	 */
	public double getCellSize(int i) {
		return _cellSize[i];
	}

	/**
	 * Returns the volume of a cell
	 * 
	 * @return the cell volume
	 */
	public double getCellVolume() {
		double result = 1;
		for (int i = 0; i < dimension; i++) {
			result *= getCellSize(i);
		}
		return result;
	}

	/**
	 * Converts a linear index to component index (a tuple of integers) in the
	 * context of the topology and with column major order
	 * 
	 * @param lidx
	 *            the linear index
	 * @return the component index
	 */
	public int[] getCompIdx(int lidx) {
		int[] result = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			result[i] = (lidx % getStride(i + 1)) / getStride(i) + _origin[i];
		}
		return result;
	}

	/**
	 * Returns a topology containing all possible cell distances as positions.
	 * The result is a topology with similar cell sizes whose cell count is
	 * doubled in every dimension an is symmetrical in the coordinate origin.
	 * 
	 * @return the topology
	 */
	public Topology getDistanceTopology() {
		int[] cellCount = new int[dimension];
		int[] origin = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			cellCount[i] = 2 * getCellCount(i) - 1;
			origin[i] = 1 - getCellCount(i);
		}
		return new Topology(cellCount, _cellSize, origin);
	}

	/**
	 * converts a component index (a tuple of integers) to a linear index in the
	 * context of the topology and with column major order
	 * 
	 * @param cidx
	 *            the component index
	 * @return the linear index
	 */
	public int getLinearIdx(int[] cidx) {
		// TODO check dimension of cidx
		int result = 0;
		for (int i = 0; i < dimension; i++) {
			result += (cidx[i] - _origin[i]) * getStride(i);
		}
		return result;
	}

	/**
	 * Returns the highest index within the topology in a given direction
	 * 
	 * @param i
	 *            the direction (dimension)
	 * @return the index
	 */
	public int getMaxIndex(int i) {
		return _origin[i] + _cellCount[i];
	}

	/**
	 * Returns an array of possible neighbor configurations of a cell.
	 * <p>
	 * For example
	 * <code>[-stride[0], stride[0], -stride[1], stride[1] ...]</code> for a
	 * cell that has all neighbors and
	 * <code>[0, stride[0], -stride[1], stride[1] ...]</code> that misses the
	 * 'left' neighbor in the first direction.
	 * <p>
	 * The result solely depends on the dimension of the topology
	 * 
	 * @return the neighbor configurations
	 */
	protected int[][] getNeighborConfigurations() {
		int[][] result = new int[1 << (dimension * 2)][dimension * 2];

		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < dimension * 2; j++) {
				result[i][j] = (i >> j) & 1;
				result[i][j] *= _stride[j / 2] * (j % 2 * 2 - 1);
			}
		}
		return result;
	}

	/**
	 * Returns the neighbor configurations for this topology as an array. The
	 * first index is the linear index of the cell, the second index addresses
	 * the neighbor as described in <code>getNeighborConfiguration</code>.
	 * 
	 * @return the neighbor strides
	 */
	public int[][] getNeighborStrides() {
		if (_neighborStrides != null) return _neighborStrides;

		int[][] config = getNeighborConfigurations();
		int[][] result = new int[totalCellCount][];

		// iterate over cells
		for (int lidx = 0; lidx < totalCellCount; lidx++) {
			int[] cidx = getCompIdx(lidx);

			// iterate over topology dimensions
			int configIdx = 0;
			for (int i = 0; i < dimension; i++) {
				// left neighbor
				cidx[i] -= 1;
				if (hasCidx(cidx)) configIdx += 1 << (2 * i);

				// right neighbor
				cidx[i] += 2;
				if (hasCidx(cidx)) configIdx += 1 << (2 * i + 1);

				// reset
				cidx[i] -= 1;
			}
			result[lidx] = config[configIdx];
		}

		_neighborStrides = result;
		return result;
	}

	/**
	 * Returns a copy of the origin array
	 * 
	 * @return the origin array
	 */
	public int[] getOrigin() {
		return _origin.clone();
	}

	/**
	 * Returns a component of the origin
	 * 
	 * @param i
	 *            the index of the component
	 * @return the origin component
	 */
	public int getOrigin(int i) {
		return _origin[i];
	}

	/**
	 * Converts a linear index to a position (a tuple of doubles, representing
	 * the position regarding the cell sizes)
	 * 
	 * @param lidx
	 *            the linear index
	 * @return the position
	 */
	public double[] getPosition(int lidx) {
		return getPosition(getCompIdx(lidx));
	}

	/**
	 * Converts a component index (a tuple of integers) to a position (a tuple
	 * of doubles, representing the position regarding the cell sizes)
	 * 
	 * @param cidx
	 *            the component index
	 * @return the position
	 */
	public double[] getPosition(int[] cidx) {
		double[] result = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			result[i] = cidx[i] * getCellSize(i);
		}
		return result;
	}

	/**
	 * Returns the squared edge length of a cell
	 * 
	 * @param component
	 *            the direction/dimension
	 * @return the edge length
	 */
	public double getSquaredCellSize(int component) {
		return _squaredCellSize[component];
	}

	/**
	 * Returns a copy of the stride array
	 * 
	 * @return all strides in an array
	 */
	public int[] getStride() {
		return _stride.clone();
	}

	/**
	 * Returns the stride for a direction
	 * 
	 * @param i
	 *            the index of the component (the direction)
	 * @return the stride
	 */
	public int getStride(int i) {
		return _stride[i];
	}

	/**
	 * Checks whether the topology includes a cell with a certain component
	 * index
	 * 
	 * @param cidx
	 *            the component index
	 * @return <code>true</code> if cell exists, <code>false</code> otherwise
	 */
	public boolean hasCidx(int[] cidx) {
		for (int i = 0; i < cidx.length; i++) {
			if (cidx[i] < _origin[i]) return false;
			if (cidx[i] > _origin[i] + _cellCount[i] - 1) return false;
		}
		return true;
	}

	/**
	 * Returns a similar topology with the origin set to 0
	 * 
	 * @return the resulting topology
	 */
	public Topology resetOrigin() {
		return applyOrigin(new int[] { 0, 0, 0 });
	}

	public String toString() {
		StringBuffer origin = new StringBuffer("Origin: ");
		StringBuffer cellCount = new StringBuffer("Cell Count: ");
		StringBuffer cellSize = new StringBuffer("Cell Size: ");

		for (int i = 0; i < dimension; i++) {
			origin.append(_origin[i] + ", ");
			cellCount.append(_cellCount[i] + ", ");
			cellSize.append(_cellSize[i] + ", ");
		}
		return origin.append(cellCount).append(cellSize).toString();
	}
}
