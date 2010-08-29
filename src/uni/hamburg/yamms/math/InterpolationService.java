package uni.hamburg.yamms.math;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator;

/**
 * Methods for interpolation of <code>RealVectorField</code>s (changing the
 * resolution of the discretization)
 * 
 * @author Claas Abert
 */
public class InterpolationService {
	/**
	 * Helper method for <code>values2slices</code> and
	 * <code>slices2values</code>. Calculates new strides for the linear storage
	 * of the non sliced dimensions.
	 * 
	 * @param strides
	 *            the source strides
	 * @param dim
	 *            the dimension that should be extraced
	 * @return the resulting strides
	 */
	private static int[] getSliceStrides(int[] strides, int dim) {
		int sliceSize = strides[dim + 1] / strides[dim];

		// get strides for slice
		int[] sstrides = strides.clone();
		sstrides[dim] = 0;
		for (int i = dim + 1; i < strides.length; i++) {
			sstrides[i] = strides[i] / sliceSize;
		}
		return sstrides;
	}

	/**
	 * Returns the position of the maximum of a field component
	 * 
	 * @param field
	 *            the field
	 * @param dim
	 *            the component to be maximized
	 * @return the position of the maximum
	 */
	public static double[] getMaxPosition(RealVectorField field, int dim) {
		try {
			double[] result = new double[field.topology.dimension];
			int lidx = 0;

			// get max cell
			double maxValue = 0;
			double[] values = field.getComponent(dim);
			for (int i = 0; i < values.length; i++) {
				if (Math.abs(values[i]) > maxValue) {
					maxValue = Math.abs(values[i]);
					lidx = i;
				}
			}

			// Interpolate
			UnivariateRealInterpolator interpolator = new SplineInterpolator();
			for (int i = 0; i < field.topology.dimension; i++) {
				if (field.topology.getCellCount(i) == 1) continue;
				double[] positions = getPositions(field.topology.getCellCount(i));
				double[] src = new double[field.topology.getCellCount(i)];

				int[] cidx = field.topology.getCompIdx(lidx);
				for (int j = 0; j < src.length; j++) {
					cidx[i] = j;
					src[j] = field.getValue(dim, cidx);
				}
				double[] padded = getPaddedValues(src);

				UnivariateRealFunction fn = interpolator.interpolate(positions, padded);
				double max = findMaximum(fn, field.topology.getCompIdx(lidx)[i] + 0.5);
				result[i] = (max + field.topology.getOrigin(i)) * field.topology.getCellSize(i);
			}

			return result;
		} catch (MathException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Finds the maximum of a UnivariateRealFunction with a very simple
	 * approximation method
	 * 
	 * @param fn
	 *            the function to maximize
	 * @param start
	 *            the starting point for the maximization
	 * @return the maximum
	 * @throws FunctionEvaluationException
	 */
	static public double findMaximum(UnivariateRealFunction fn, double start)
			throws FunctionEvaluationException {
		double pos = start;
		double last = fn.value(start);

		double inc = 0.5;

		while (Math.abs(inc) > 0.00000001) {
			pos += inc;
			if (fn.value(pos) < last) {
				inc = -inc / 2;
			}
			last = fn.value(pos);
		}
		return pos;
	}

	/**
	 * Changes the resolution of the field in one direction. The field is
	 * interpolated with a third order approximation.
	 * 
	 * @param dim
	 *            the direction (dimension) to be scaled
	 * @param cellCount
	 *            the new cell count in the direction
	 * @return the resulting field
	 */
	public static RealVectorField interpolate(RealVectorField field, int dim, int cellCount) {
		try {
			// initialize
			UnivariateRealInterpolator interpolator = new SplineInterpolator();
			// double ratio = (field.topology.getCellCount(dim) - 1.0) /
			// (cellCount - 1.0);
			double ratio = 1.0 * field.topology.getCellCount(dim) / cellCount;

			// new topology
			int[] cellCounts = field.topology.getCellCount();
			cellCounts[dim] = cellCount;
			double[] cellSizes = field.topology.getCellSize();
			cellSizes[dim] = cellSizes[dim] * ratio;
			// be careful, origin scaling might fail (int cast)
			int[] origins = field.topology.getOrigin();
			origins[dim] = (int) (origins[dim] * ratio);

			Topology destTopology = new Topology(cellCounts, cellSizes, origins);

			// initialize positions array for apache interpolator
			double[] positions = getPositions(field.topology.getCellCount(dim));

			// slice values
			double[][] result = new double[field.dimension][];

			for (int i = 0; i < field.dimension; i++) {
				// transpose
				double[][] src = values2slices(field.getComponent(i), field.topology.getStride(),
						dim);
				double[][] dest = new double[src.length][cellCount];

				// interpolate
				for (int j = 0; j < src.length; j++) {
					double[] padded = getPaddedValues(src[j]);
					UnivariateRealFunction fn = interpolator.interpolate(positions, padded);
					for (int k = 0; k < cellCount; k++) {
						dest[j][k] = fn.value(ratio * (k + 0.5));
					}
				}

				// transpose back
				result[i] = slices2values(dest, destTopology.getStride(), dim);
			}

			return new RealVectorField(destTopology, result);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Get Positions Array used for the apache interpolator
	 * 
	 * @param cellCount
	 *            the cell count in a certain direction
	 * @return the position array
	 */
	public static double[] getPositions(int cellCount) {
		double[] positions = new double[cellCount + 2];
		positions[0] = 0;
		positions[positions.length - 1] = positions.length;
		for (int i = 0; i < positions.length - 2; i++) {
			positions[i + 1] = i + 0.5;
		}
		return positions;
	}

	/**
	 * Pads the values of a single data row to comply with the positions array
	 * (the first and last values are duplicated)
	 * 
	 * @param values
	 *            the values
	 * @return the padded values
	 */
	public static double[] getPaddedValues(double[] values) {
		double[] padded = new double[values.length + 2];
		padded[0] = values[0];
		padded[padded.length - 1] = values[values.length - 1];
		for (int k = 0; k < values.length; k++) {
			padded[k + 1] = values[k];
		}
		return padded;
	}

	/**
	 * Triggers the direction based interpolate method to change the resolution
	 * of the field in all three directions
	 * 
	 * @param cellCounts
	 *            array of new cell counts
	 * @return the resulting field
	 */
	public static RealVectorField interpolate(RealVectorField field, int[] cellCounts) {
		assert field.topology.dimension == cellCounts.length;

		RealVectorField result = field;
		for (int i = 0; i < cellCounts.length; i++) {
			if (cellCounts[i] == result.topology.getCellCount(i)) continue;
			result = interpolate(result, i, cellCounts[i]);
		}

		return result;
	}

	/**
	 * Reverse action of <code>values2slices</code>.
	 * 
	 * @param slices
	 *            the two dimensional slice array
	 * @param strides
	 *            the strides
	 * @param dim
	 *            the target dimension of the extracted slices
	 * @return the resulting linearized matrix
	 */
	private static double[] slices2values(double[][] slices, int[] strides, int dim) {
		// get strides for slice
		int[] sstrides = getSliceStrides(strides, dim);

		// transpose
		double[] result = new double[slices.length * slices[0].length];
		for (int i = 0; i < result.length; i++) {
			int si = 0;
			for (int j = 0; j < strides.length - 1; j++) {
				si += (i % strides[j + 1]) / strides[j] * sstrides[j];
			}
			result[i] = slices[si][(i % strides[dim + 1]) / strides[dim]];
		}

		return result;
	}

	/**
	 * Extracts the one dimensional chunks in one specified dimension of a
	 * linearly stored matrix and stores them in a two dimension array.
	 * <p>
	 * For example the 3 dimension matrix
	 * 
	 * <pre>
	 *  10   11   12
	 * 1    2    3
	 * 
	 *  13   14   15
	 * 4    5    6
	 * 
	 *  16   16   17
	 * 7    8    9
	 * </pre>
	 * 
	 * is linearly represented by <code>[1, 2, 3, 4, 5, ...]</code> and
	 * transformed to <code>[[1, 2, 3], [4, 5, 6], ...]</code> or
	 * <code>[[1, 4, 5], [2, 5, 8], ...]</code> or
	 * <code>[[1, 10], [2, 11], ...]</code>.
	 * 
	 * 
	 * @param values
	 *            the linear matrix data
	 * @param strides
	 *            the strides of the matrix
	 * @param dim
	 *            the dimension to be extracted
	 * @return the resulting two dimensional array
	 */
	private static double[][] values2slices(double[] values, int[] strides, int dim) {
		int sliceSize = strides[dim + 1] / strides[dim];
		int sliceCount = values.length / sliceSize;

		// get strides for slice
		int[] sstrides = getSliceStrides(strides, dim);

		// transpose
		double[][] result = new double[sliceCount][sliceSize];
		for (int i = 0; i < values.length; i++) {
			int si = 0;
			for (int j = 0; j < strides.length - 1; j++) {
				si += (i % strides[j + 1]) / strides[j] * sstrides[j];
			}
			result[si][(i % strides[dim + 1]) / strides[dim]] = values[i];
		}

		return result;
	}
}
