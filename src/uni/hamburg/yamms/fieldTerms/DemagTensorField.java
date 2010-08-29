package uni.hamburg.yamms.fieldTerms;

import uni.hamburg.yamms.math.ComplexTensorField;
import uni.hamburg.yamms.math.Topology;
import JSci.maths.MathDouble;

/**
 * Represents the demagnetization tensor field and offers static members for the
 * calculation of the tensor field from a topology using the method introduced
 * by Newell et al.
 * 
 * @author Claas Abert
 * 
 */
public class DemagTensorField {
	/**
	 * Calculates the tensor field for a topology a returns the result
	 * 
	 * @param topology
	 *            the topology
	 * @return the resulting tensor field
	 */
	public static ComplexTensorField fromTopology(Topology topology) {
		Topology distanceTopology = topology.getDistanceTopology();
		return new ComplexTensorField(
				distanceTopology,
				calculateValues(distanceTopology),
				new int[] {
					0, 1, 2,
					1, 3, 4,
					2, 4, 5
				});
	}

	/**
	 * Calculates the values array of the demag tensor form a topology
	 * 
	 * @param topology
	 *            the topology
	 * @return the values
	 */
	protected static double[][] calculateValues(Topology topology) {
		double[][] values = new double[6][topology.totalCellCount * 2];
		
		// iterate over all possible distances
		for (int i = 0; i < topology.totalCellCount; i++) {
			// iterate over the tensor components (xx, xy, xz, yy, yz, zz)
			for (int j = 0; j < 6; j++) {
				values[j][i*2] = getTensorComponent(
						topology.getPosition(i), // the distance vector
						topology.getCellSize(),  // cell size (dx, dy, dz)
						j);                      // tensor component
			}
		}
		return values;
	}

	/**
	 * Calculates the value of a single component of the tensor field in a
	 * certain cell
	 * 
	 * @param x
	 *            the position of the cell
	 * @param dx
	 *            the size of the cell
	 * @param component
	 *            the component to calculate
	 * @return the value
	 */
	protected static double getTensorComponent(double[] x, double[] dx, int component) {
		switch (component) {
		case 0:
			return getNxx(x, dx);
		case 1:
			return getNxy(x, dx);
		case 2:
			return getNxz(x, dx);
		case 3:
			return getNyy(x, dx);
		case 4:
			return getNyz(x, dx);
		case 5:
			return getNzz(x, dx);
		default:
			return 0;
		}
	}
	
	/**
	 * Returns coefficients used by the <code>getNxx</code> and
	 * <code>getNxy</code> methods
	 * 
	 * @return the coefficients
	 */
	protected static int[] getCoefficients() {
		return new int[] {
				 8,  0,  0,  0,   -4,  1,  0,  0,   -4, -1,  0,  0,   -4,  0,  1,  0,
				-4,  0, -1,  0,   -4,  0,  0,  1,   -4,  0,  0, -1,    2,  1,  1,  0,
				 2,  1, -1,  0,    2, -1,  1,  0,    2, -1, -1,  0,    2,  1,  0,  1,
				 2,  1,  0, -1,    2, -1,  0,  1,    2, -1,  0, -1,    2,  0,  1,  1,
				 2,  0,  1, -1,    2,  0, -1,  1,    2,  0, -1, -1,   -1,  1,  1,  1,
				-1,  1,  1, -1,   -1,  1, -1,  1,   -1,  1, -1, -1,   -1, -1,  1,  1,
				-1, -1,  1, -1,   -1, -1, -1,  1,   -1, -1, -1, -1
			};
	}
  
	/**
	 * Calculates the <code>xx<code> component
	 * 
	 * @param x
	 *            the position of the cell
	 * @param dx
	 *            the size of the cell
	 * @return the value of the component
	 */
	protected static double getNxx(double[] x, double[] dx) {
		int[] c = getCoefficients();
		double result = 0;
		for (int i = 0; i < c.length; i += 4) {
			result += c[i] * newell_f(
					x[0] + c[i + 1] * dx[0],
					x[1] + c[i + 2] * dx[1],
					x[2] + c[i + 3] * dx[2]);
		}

		return result / (4 * Math.PI * dx[0] * dx[1] * dx[2]);
	}
	
	/**
	 * Calculates the <code>xy<code> component
	 * 
	 * @param x
	 *            the position of the cell
	 * @param dx
	 *            the size of the cell
	 * @return the value of the component
	 */
	protected static double getNxy(double[] x, double dx[]) {
		int[] c = getCoefficients();
		double result = 0;
		for (int i = 0; i < c.length; i += 4) {
			result += c[i] * newell_g(
					x[0] + c[i + 1] * dx[0],
					x[1] + c[i + 2] * dx[1],
					x[2] + c[i + 3] * dx[2]);
		}
		return result / (4 * Math.PI * dx[0] * dx[1] * dx[2]);
	}

	/**
	 * Calculates the <code>xz<code> component
	 * 
	 * @param x
	 *            the position of the cell
	 * @param dx
	 *            the size of the cell
	 * @return the value of the component
	 */
	protected static double getNxz(double[] x, double dx[]) {
		return getNxy(new double[] { x[0], x[2], x[1] }, new double[] { dx[0],
				dx[2], dx[1] });
	}

	/**
	 * Calculates the <code>yy<code> component
	 * 
	 * @param x
	 *            the position of the cell
	 * @param dx
	 *            the size of the cell
	 * @return the value of the component
	 */
	protected static double getNyy(double[] x, double[] dx) {
		return getNxx(new double[] { x[1], x[0], x[2] }, new double[] { dx[1],
				dx[0], dx[2] });
	}

	/**
	 * Calculates the <code>yz<code> component
	 * 
	 * @param x
	 *            the position of the cell
	 * @param dx
	 *            the size of the cell
	 * @return the value of the component
	 */
	protected static double getNyz(double[] x, double[] dx) {
		return getNxy(new double[] { x[1], x[2], x[0] }, new double[] { dx[1],
				dx[2], dx[0] });
	}

	/**
	 * Calculates the <code>zz<code> component
	 * 
	 * @param x
	 *            the position of the cell
	 * @param dx
	 *            the size of the cell
	 * @return the value of the component
	 */
	protected static double getNzz(double[] x, double[] dx) {
		return getNxx(new double[] { x[2], x[1], x[0] }, new double[] { dx[2],
				dx[1], dx[0] });
	}
  
	/**
	 * Calculates the 'f function' from the Newell method
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 * @return the result
	 */
	protected static double newell_f(double x, double y, double z) {
		x = Math.abs(x);
		y = Math.abs(y);
		z = Math.abs(z);
		double x2 = Math.pow(x, 2);
		double y2 = Math.pow(y, 2);
		double z2 = Math.pow(z, 2);
		double R = Math.sqrt(x2 + y2 + z2);
		double result = 0;

		if (x2 + z2 > 0)
			result += (y / 2.0) * (z2 - x2)
					* MathDouble.asinh(y / (Math.sqrt(x2 + z2))).doubleValue();
		if (x2 + y2 > 0)
			result += (z / 2.0) * (y2 - x2)
					* MathDouble.asinh(z / (Math.sqrt(x2 + y2))).doubleValue();
		if (x * R > 0)
			result -= x * y * z
					* MathDouble.atan((y * z) / (x * R)).doubleValue();
		result += ((1 / 6.0) * (2 * x2 - y2 - z2) * R);
		return result;
	}

	/**
	 * Calculates the 'g function' from the Newell method
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 * @return the result
	 */
	protected static double newell_g(double x, double y, double z) {
		z = Math.abs(z);
		double x2 = Math.pow(x, 2);
		double y2 = Math.pow(y, 2);
		double z2 = Math.pow(z, 2);
		double R = Math.sqrt(x2 + y2 + z2);

		double result = - (x * y * R / 3);

		if (x2 + y2 > 0)
			result += (x * y * z)
					* MathDouble.asinh(z / Math.sqrt(x2 + y2)).doubleValue();

		if (x2 + z2 > 0)
			result += (x / 6) * (3 * z2 - x2)
					* MathDouble.asinh(y / (Math.sqrt(x2 + z2))).doubleValue();

		if (y2 + z2 > 0)
			result += (y / 6) * (3 * z2 - y2)
					* MathDouble.asinh(x / (Math.sqrt(y2 + z2))).doubleValue();

		if (x * R != 0)
			result -= ((z * (x2)) / 2) * Math.atan((y * z) / (x * R));

		if (Math.abs(y * R) > 0)
			result -= ((z * (y2)) / 2) * Math.atan((x * z) / (y * R));

		if (Math.abs(z * R) > 0)
			result -= ((z2 * z) / 6) * Math.atan((x * y) / (z * R));

		return result;
	}

}