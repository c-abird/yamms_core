package uni.hamburg.yamms.math;

/**
 * N-dimensional vector field in the real space
 * 
 * @author Claas Abert
 * 
 */
public class RealVectorField extends Field {
	/**
	 * Static method that returns a real vector field that whose value is 0 at
	 * every point
	 * 
	 * @param t
	 *            the topology
	 * @param dimension
	 *            the dimension of the field
	 * @return the real vector field
	 */
	static public RealVectorField getEmptyField(Topology t, int dimension) {
		return new RealVectorField(t, new double[dimension][t.totalCellCount]);
	}

	/**
	 * Static method that creates a uniform (spatially constant) field on a
	 * certain topology
	 * 
	 * @param t
	 *            the topology
	 * @param vector
	 *            the value of the field
	 * @return the real vector field
	 */
	static public RealVectorField getUniformField(Topology t, double[] vector) {
		double[][] values = new double[vector.length][t.totalCellCount];
		for (int i = 0; i < vector.length; i++) {
			for (int j = 0; j < t.totalCellCount; j++) {
				values[i][j] = vector[i];
			}
		}
		return new RealVectorField(t, values);
	}

	/**
	 * Constructor for linearized value support
	 * 
	 * @param topology
	 *            the topology
	 * @param values
	 *            values of the vector field (column major order, the last index
	 *            is the component of the field)
	 */
	public RealVectorField(Topology topology, double[] values) {
		super(topology, values);
	}

	/**
	 * Standard constructor
	 * 
	 * @param topology
	 *            the topology
	 * @param values
	 *            values of the vector field (1st dimension of the array is the
	 *            component of the field, 2nd dimension are the field values in
	 *            column major order)
	 */
	public RealVectorField(Topology topology, double[][] values) {
		super(topology, values);

		// check precondition (super must be the first call)
		assert topology.totalCellCount == values[0].length;
	}

	/**
	 * Adds another real vector field and returns the result
	 * 
	 * @param vf
	 *            the real vector field to be added
	 * @return the sum of the vector fields
	 */
	public RealVectorField add(RealVectorField vf) {
		double[][] result = new double[dimension][topology.totalCellCount];
		for (int i = 0; i < vf.dimension; i++) {
			for (int j = 0; j < vf.topology.totalCellCount; j++) {
				result[i][j] = getValue(i, j) + vf.getValue(i, j);
			}
		}
		return new RealVectorField(topology, result);
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
	public RealVectorField applyTopology(final Topology newTopology) {
		assert topology.dimension == newTopology.dimension;
		// TODO handle change of cell size

		final double[][] result = new double[dimension][newTopology.totalCellCount];

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
		return new RealVectorField(newTopology, result);
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
				System.arraycopy(source, sOffs, target, tOffs, interval[0]);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public RealVectorField clone() {
		return new RealVectorField(topology, getValuesCopy());
	}

	/**
	 * Calculates the point-wise cross product with another vector field
	 * 
	 * @param vf
	 *            the field to be multiplied
	 * @return the resulting field
	 */
	public RealVectorField cross(final RealVectorField vf) {
		assert topology.equals(vf.topology);
		assert dimension == 3 : "Dimension must be 3";
		assert vf.dimension == 3 : "Dimension must be 3";

		final double[][] result = new double[dimension][topology.totalCellCount];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < topology.totalCellCount; j++) {
				result[i][j] = getValue((i + 1) % 3, j) * vf.getValue((i + 2) % 3, j)
						- getValue((i + 2) % 3, j) * vf.getValue((i + 1) % 3, j);
			}
		}
		return new RealVectorField(topology, result);
	}

	/**
	 * Divides the field point-wise with a scalar field and returns the
	 * resulting field
	 * 
	 * @param sf
	 *            the field to be divided by
	 * @return the resulting field
	 * @deprecated
	 */
	public RealVectorField divideBy(RealScalarField sf) {
		assert topology.equals(sf.topology);

		double[][] result = new double[dimension][topology.totalCellCount];

		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < topology.totalCellCount; j++) {
				if (sf.getValue(j) == 0) continue;
				result[i][j] = getValue(i,j) / sf.getValue(j);
			}
		}
		return new RealVectorField(topology, result);
	}

	/**
	 * Calculates the dot product with a vector and return the resulting scalar
	 * field
	 * 
	 * @param vec
	 *            the vector
	 * @return the scalar field
	 */
	public RealScalarField dot(double[] vec) {
		assert dimension == vec.length;

		double[] result = new double[topology.totalCellCount];
		for (int dim = 0; dim < dimension; dim++) {
			for (int i = 0; i < topology.totalCellCount; i++) {
				result[i] += vec[dim] * getValue(dim, i);
			}
		}
		return new RealScalarField(topology, result);
	}

	/**
	 * Calculates the directional derivative of the field with a nearest
	 * neighbor methos and returns the result
	 * 
	 * @param direction
	 *            the direction of the derivative
	 * @return the resulting field
	 */
	public RealVectorField firstDerivative(int direction) {
		double[][] result = new double[dimension][topology.totalCellCount];
		int[][] neighborStrides = topology.getNeighborStrides();

		for (int dim = 0; dim < dimension; dim++) {
			for (int lidx = 0; lidx < topology.totalCellCount; lidx++) {
				int dx = 0;
				int c = 0;
				for (int i = 0; i < 2; ++i) {
					int stride = neighborStrides[lidx][direction * 2 + i];
					if (stride == 0) continue;
					dx += (getValue(dim, lidx + stride) - getValue(dim, lidx)) * (2 * i - 1);
					++c;
				}
				if (c != 0) result[dim][lidx] = dx / topology.getCellSize(direction) / c;
			}
		}

		return new RealVectorField(topology, result);
	}

	/**
	 * Calculates the average vector of the field
	 * 
	 * @return the average vector
	 */
	public double[] getAverage() {
		// TODO cache
		double[] result = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < topology.totalCellCount; j++) {
				result[i] += getValue(i, j);
			}
			result[i] /= topology.totalCellCount;
		}
		return result;
	}

	/**
	 * Calculates the average norm of the field
	 * 
	 * @return the norm
	 */
	public double getAverageNorm() {
		// TODO cache
		double result = 0;
		for (int i = 0; i < dimension; i++) {
			double comp = 0;
			for (int j = 0; j < topology.totalCellCount; j++) {
				comp += getValue(i, j);
			}
			result += Math.pow(comp, 2);
		}
		return Math.sqrt(result) / topology.totalCellCount;
	}

	/**
	 * Returns a single component of the vector field as a scalar field
	 * 
	 * @param dim
	 *            the component
	 * @return the scalar field
	 */
	public RealScalarField getComponentScalarField(int dim) {
		return new RealScalarField(topology, new double[][] { _values[dim] });
	}

	/**
	 * Retrieves the maximum norm of the field
	 * 
	 * @return the norm
	 */
	public double getMaxNorm() {
		// TODO cache
		double result = 0;
		for (int i = 0; i < topology.totalCellCount; i++) {
			double norm = 0;
			for (int dim = 0; dim < dimension; dim++) {
				norm += Math.pow(getValue(dim, i), 2);
			}
			if (result < norm) result = norm;
		}
		return Math.sqrt(result);
	}

	/**
	 * Returns the norm of the vector at a position specified with an linear
	 * index.
	 * 
	 * @param lidx
	 *            the linear index of the position
	 * @return the norm
	 */
	public double getNorm(int lidx) {
		double norm = 0;
		for (int dim = 0; dim < dimension; ++dim) {
			norm += Math.pow(getValue(dim, lidx), 2);
		}
		return Math.sqrt(norm);
	}

	/**
	 * Returns a scalar field with the norm of the vector field at any position
	 * 
	 * @return the scalar field
	 */
	public RealScalarField getNormField() {
		double[] result = new double[topology.totalCellCount];
		for (int i = 0; i < topology.totalCellCount; i++) {
			result[i] = getNorm(i);
		}
		return new RealScalarField(topology, result);
	}

	/**
	 * Calculates the laplace (second derivative) of the field with a nearest
	 * neighbor method
	 * 
	 * @return the resulting field
	 */
	public RealVectorField laplace() {
		BooleanField bounds = new BooleanConstantField(topology, true);
		return laplaceWithBounds(bounds);
	}

	/**
	 * Calculates the second derivative of the field with a nearest neighbor
	 * method. Takes a <code>bounds</code> argument for limiting the calculation
	 * of the derivative to certain areas.
	 * 
	 * @param bounds
	 *            a boolean field (<code>true</code>: is taken into account,
	 *            <code>false</code>: is not taken into account)
	 * @return the resulting field
	 */
	public RealVectorField laplaceWithBounds(BooleanField bounds) {
		assert bounds.topology.equals(topology);

		double[][] result = new double[dimension][topology.totalCellCount];
		int[][] neighborStrides = topology.getNeighborStrides();

		for (int dim = 0; dim < dimension; dim++) {
			for (int lidx = 0; lidx < topology.totalCellCount; lidx++) {
				if (!bounds.getValue(lidx)) continue;
				for (int i = 0; i < neighborStrides[lidx].length; i++) {
					if (neighborStrides[lidx][i] == 0
							|| !bounds.getValue(lidx + neighborStrides[lidx][i])) continue;

					result[dim][lidx] += (getValue(dim, lidx + neighborStrides[lidx][i]) - getValue(
							dim, lidx))
							/ topology.getSquaredCellSize(i / 2);
				}
			}
		}

		return new RealVectorField(topology, result);
	}

	/**
	 * Normalizes the field to the given norm and returns the resulting field
	 * 
	 * @param norm
	 *            the norm
	 * @return the resulting field
	 */
	public RealVectorField normTo(double norm) {
		double[][] result = new double[dimension][topology.totalCellCount];
		for (int i = 0; i < topology.totalCellCount; i++) {
			// get current norm
			double current = 0;
			for (int j = 0; j < dimension; j++) {
				current += Math.pow(getValue(j, i), 2);
			}

			// apply norm
			double factor = norm / Math.sqrt(current);
			for (int j = 0; j < dimension; j++) {
				result[j][i] = getValue(j, i) * factor;
			}
		}
		return new RealVectorField(topology, result);
	}

	/**
	 * Normalizes the field point wise to the given "scalar norm field" and
	 * returns the resulting field
	 * 
	 * @param norm
	 *            the norm as a scalar field
	 * @return the resulting field
	 */
	public RealVectorField normTo(RealScalarField norm) {
		double[][] result = new double[dimension][topology.totalCellCount];
		for (int i = 0; i < topology.totalCellCount; i++) {
			// get current norm
			double current = 0;
			for (int j = 0; j < dimension; j++) {
				current += Math.pow(getValue(j, i), 2);
			}
			if (current == 0) continue; // TODO find a better way?

			// apply norm
			double factor = norm.getValue(i) / Math.sqrt(current);
			for (int j = 0; j < dimension; j++) {
				result[j][i] = getValue(j, i) * factor;
			}
		}
		return new RealVectorField(topology, result);
	}

	/**
	 * Multiplies the field with a scalar value and returns the resulting field
	 * 
	 * @param fac
	 *            the factor
	 * @return the resulting field
	 */
	public RealVectorField times(double fac) {
		double[][] result = new double[dimension][topology.totalCellCount];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < topology.totalCellCount; j++) {
				result[i][j] = fac * getValue(i, j);
			}
		}
		return new RealVectorField(topology, result);
	}

	/**
	 * Multiplies the field point-wise with a scalar field and returns the
	 * resulting field
	 * 
	 * @param sf
	 *            the field to be multiplied
	 * @return the resulting field
	 */
	public RealVectorField times(RealScalarField sf) {
		assert topology.equals(sf.topology);

		double[][] result = new double[dimension][topology.totalCellCount];

		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < topology.totalCellCount; j++) {
				result[i][j] = getValue(i, j) * sf.getValue(j);
			}
		}
		return new RealVectorField(topology, result);
	}

	/**
	 * Converts the real vector field to a complex vector field and returns the
	 * resulting field
	 * 
	 * @return the resulting field
	 */
	public ComplexVectorField toComplexVectorField() {
		double[][] result = new double[dimension][topology.totalCellCount * 2];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < topology.totalCellCount; j++) {
				result[i][2 * j] = getValue(i, j);
			}
		}
		return new ComplexVectorField(topology, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < topology.totalCellCount; i++) {
			for (int j = 0; j < dimension; j++) {
				result.append("" + getValue(j, i) + ", ");
			}
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Returns the vector at a position defined by a linear index
	 * 
	 * @param lidx
	 *            the linear index
	 * @return the vector
	 */
	public double[] getVector(int lidx) {
		double[] result = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			result[i] = getValue(i, lidx);
		}
		return result;
	}
	
	/**
	 * Returns the vector at a position defined by a component index
	 * 
	 * @param cidx
	 *            the component index
	 * @return the vector
	 */
	public double[] getVector(int[] cidx) {
		return getVector(topology.getLinearIdx(cidx));
	}
}
