package uni.hamburg.yamms.math.fft;

/**
 * Specifies a tranform dimension, including the dimension length and the
 * input/output strides. (See the FFTW3 documentation.)
 * 
 * @author Gunnar Selke
 */
public class Dimension {
	/** size of the dimension */
	public final int n;
	/** input stride */
	public final int is;
	/** output stride */
	public final int os;

	/**
	 * Constructor.
	 * 
	 * @param n
	 *            size of the dimension
	 * @param is
	 *            input stride length
	 * @param os
	 *            output stride length
	 */
	public Dimension(int n, int is, int os) {
		this.n = n;
		this.is = is;
		this.os = os;
	}

	/**
	 * Creates a pack of dimensions representing a column-major layout.
	 * 
	 * @param sizes
	 *            lengths of the dimensions
	 * @return an array of the created dimensions
	 */
	static Dimension[] colMajor(int... sizes) {
		Dimension dims[] = new Dimension[sizes.length];
		for (int i = 0; i < sizes.length; ++i) {
			int stride = 1;
			if (i > 0) {
				stride = dims[i - 1].is * dims[i - 1].n;
			}
			dims[i] = new Dimension(sizes[i], stride, stride);
		}
		return dims;
	}

	/**
	 * Determines the number of points of the space spanned by a list of
	 * dimensions
	 * 
	 * @param dims
	 *            the dimensions
	 * @return the product of the lengths of the dimensions
	 */
	static public int getN(Dimension[] dims) {
		int n = 1;
		for (Dimension dim : dims)
			n *= dim.n;
		return n;
	}
}
