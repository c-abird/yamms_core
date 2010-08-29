package uni.hamburg.yamms.math.fft;

/**
 * Represents the Fourier transform specification for objects derived from the
 * FFT class. This interface is inspired by the "guru interface" from the FFTW3
 * library (www.fftw.org).
 * 
 * @author Gunnar Selke
 */
public class Spec {
	/** simplicity :) */
	private boolean simple;
	/** type of transform. Sets forward/backward and C->C / C->R / R->C ... */
	private Type type;
	/** dimensions of each iterated Fourier transform */
	private Dimension[] transform_dims;
	/**
	 * dimensions representing the starting point of the iterated Fourier
	 * transforms
	 */
	private Dimension[] loop_dims;

	/**
	 * Constructor.
	 * 
	 * @param type
	 *            one of (Type.FORW_C2C, BACK_C2C, FORW_R2C, BACK_C2R)
	 * @param transform_dims
	 *            dimensions of each iterated Fourier transform (see Dimension
	 *            class)
	 * @param loop_dims
	 *            dimensions representing the starting point of each iterated
	 *            Fourier transform
	 */
	public Spec(Type type, Dimension transform_dims[], Dimension[] loop_dims) {
		this.simple = false;
		this.type = type;
		this.transform_dims = transform_dims;
		this.loop_dims = loop_dims;
	}

	/**
	 * Constructor. Only one iterated Fourier transform.
	 * 
	 * @param type
	 *            one of (Type.FORW_C2C, BACK_C2C, FORW_R2C, BACK_C2R)
	 * @param transform_dims
	 *            dimensions of each iterated Fourier transform (see Dimension
	 *            class)
	 */
	public Spec(Type type, Dimension transform_dims[]) {
		this(type, transform_dims, new Dimension[0]);
	}

	/**
	 * Constructor. Assumes a column major arrangement of the transform
	 * dimensions.
	 * 
	 * @param type
	 *            one of (Type.FORW_C2C, BACK_C2C, FORW_R2C, BACK_C2R)
	 * @param size
	 *            number of data points in each dimension
	 */
	public Spec(Type type, int... size) {
		this(type, Dimension.colMajor(size));
		this.simple = true;
	}

	/**
	 * @return the type of the transform
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return if the transform is "simple". This is FFTW3-specific and will go
	 *         away soon.
	 */
	public boolean isSimple() {
		return simple;
	}

	/**
	 * @return the dimensions of each iterated transform.
	 */
	public Dimension[] getTransformDimensions() {
		return transform_dims;
	}

	/**
	 * @return the dimensions representing the starting points of each iterated
	 *         transform
	 */
	public Dimension[] getLoopDimensions() {
		return loop_dims;
	}

	/**
	 * @return the number of transform dimensions
	 */
	public int getTransformRank() {
		return transform_dims.length;
	}

	/**
	 * @return the number of iterated transforms
	 */
	public int getLoopRank() {
		return loop_dims.length;
	}

	/**
	 * @return the total number of transformed data points
	 */
	public int getNumDataPoints() {
		return Dimension.getN(transform_dims) * Dimension.getN(loop_dims);
	}
}
