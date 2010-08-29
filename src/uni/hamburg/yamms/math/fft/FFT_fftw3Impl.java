package uni.hamburg.yamms.math.fft;

/**
 * FFTW3 implementation of the FFT service.
 * 
 * @author Claas Abert
 * @author Gunnar Selke
 * 
 */
public class FFT_fftw3Impl extends FFT
{
	// documented flags
	public static final int FFTW_ESTIMATE = (1 << 6);
	public static final int FFTW_MEASURE = (0);
	public static final int FFTW_PATIENT = (1 << 5); /* IMPATIENT is default */
	public static final int FFTW_EXHAUSTIVE = (1 << 3); /* NO_EXHAUSTIVE is default */

	public static final int FFTW_DESTROY_INPUT = (1 << 0);
	public static final int FFTW_UNALIGNED = (1 << 1);
	public static final int FFTW_CONSERVE_MEMORY = (1 << 2);
	public static final int FFTW_PRESERVE_INPUT = (1 << 4); /* cancels FFTW_DESTROY_INPUT */

	// undocumented beyond-guru flags
	static final int FFTW_ESTIMATE_PATIENT = (1 << 7);
	static final int FFTW_BELIEVE_PCOST = (1 << 8);
	static final int FFTW_NO_DFT_R2HC = (1 << 9);
	static final int FFTW_NO_NONTHREADED = (1 << 10);
	static final int FFTW_NO_BUFFERING = (1 << 11);
	static final int FFTW_NO_INDIRECT_OP = (1 << 12);
	static final int FFTW_ALLOW_LARGE_GENERIC = (1 << 13); /* NO_LARGE_GENERIC is default */
	static final int FFTW_NO_RANK_SPLITS = (1 << 14);
	static final int FFTW_NO_VRANK_SPLITS = (1 << 15);
	static final int FFTW_NO_VRECURSE = (1 << 16);
	static final int FFTW_NO_SIMD = (1 << 17);
	static final int FFTW_NO_SLOW = (1 << 18);
	static final int FFTW_NO_FIXED_RADIX_LARGE_N = (1 << 19);
	static final int FFTW_ALLOW_PRUNING = (1 << 20);
	static final int FFTW_WISDOM_ONLY = (1 << 21);
	
	private long _inplace_plan = 0;
	private long _outofplace_plan = 0;
	private int _mode = -1;
	private int _num_threads = 1;
	
	final private int FLAGS = FFTW_MEASURE;

	/**
	 * Constructor. Sets the number of threads to 1.
	 * @param spec the transform specification
	 */
	public FFT_fftw3Impl(Spec spec)
	{
		this(spec, 1);
	}
	
	/**
	 * Constructor.
	 * @param spec the transform specification
	 * @param num_threads the number of threads
	 */
	public FFT_fftw3Impl(Spec spec, int num_threads)
	{
		super(spec);
		
		this._mode = -1;
		this._num_threads = num_threads;
		switch (spec.getType()) {
			case FORW_C2C: this._mode = MODE_C2C_FORW; break;
			case BACK_C2C: this._mode = MODE_C2C_BACKW; break;
			case FORW_R2C: this._mode = MODE_R2C; break;
			case BACK_C2R: this._mode = MODE_C2R; break;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable
	{
		if (_inplace_plan != 0) destroyPlan(_inplace_plan);
		if (_outofplace_plan != 0) destroyPlan(_outofplace_plan);
		super.finalize();
	}

	/* (non-Javadoc)
	 * @see uni.hamburg.m3sc.math.fft.FFT#transform(double[], double[])
	 */
	public void transform(double[] in, double[] out)
	{
		if (in == out) { // in-place
			if (_inplace_plan == 0)
				_inplace_plan = createPlan(true);
			fftw_execute_dft(_inplace_plan, in, out, _mode);
			
		} else { // out-of-place
			if (_outofplace_plan == 0)
				_outofplace_plan = createPlan(false);
			fftw_execute_dft(_outofplace_plan, in, out, _mode);
		}
	}
	
	/**
	 * Internal initialization function.
	 * @param inplace
	 * @return
	 */
	private long createPlan(boolean inplace)
	{
		// Setup threads.
		fftw_plan_with_nthreads(_num_threads);
		
		// TODO: Handle non-simple plan specs.
		if (!_spec.isSimple()) {
			assert false : "Not implemented!";
		}
		
		// transform type & scratch memory needed for plan construction.
		int in_size = -1;
		int out_size = -1;
		switch (this._mode) {
			case MODE_C2C_FORW:
				in_size = out_size = 2 * _spec.getNumDataPoints(); 
				break;
			case MODE_C2C_BACKW:
				in_size = out_size = 2 * _spec.getNumDataPoints();
				break;
			case MODE_R2C:
				assert false : "Not implemented!";
			case MODE_C2R:
				assert false : "Not implemented!";
		}
		
		double in[] = new double[in_size];
		double out[];
		if (inplace) {
			out = in;
		} else {
			out = new double[out_size];
		}
		
		// create plan
		long plan = fftw_plan_guru_dft(
				_spec.getTransformDimensions(), 
				_spec.getLoopDimensions(), 
				in, out,
				_mode, FLAGS);
		
		if (plan == 0) {
			throw new IllegalArgumentException("fftw plan creation failed.");
		}
		return plan;
	}
	
	/**
	 * Internal cleanup function.
	 * @param plan the plan to clean up
	 */
	private void destroyPlan(long plan)
	{
		fftw_destroy_plan(plan);
	}
	
	private static final int MODE_C2C_FORW = 0;
	private static final int MODE_C2C_BACKW = 1;
	private static final int MODE_R2C = 2;
	private static final int MODE_C2R = 3;
	
	private native long fftw_plan_guru_dft(
			Dimension transform_dims[], 
			Dimension loop_dims[],
			double[] in, double[] out,
			int mode, int flags);
	
	private native void fftw_execute_dft(long plan, double []in, double []out, int mode);
	private native void fftw_destroy_plan(long plan);
	//private native void fftw_print_plan(long plan);
	private native void fftw_plan_with_nthreads(int num_threads);
	private native static int fftw_initialize();
	
	/** set to true if FFTW3 bindings are available */
	private static boolean available = false;

	static {
		try {
			System.loadLibrary("wrapfftw3");
			int result = fftw_initialize();
			if (result != 0) {
				throw new IllegalArgumentException("libwrapfftw3 could be loaded, but sanity check failed: error code=" + result);
			}
			System.out.println("fftw3 wrapper loaded.");
			available = true;
		} catch (UnsatisfiedLinkError e) {
			// library not found
			System.out.println("fftw3 wrapper not loaded.");
			available = false;
		} catch (IllegalArgumentException e) {
			// sanity check failed
			System.out.println(e.getMessage());
			available = false;
		}
	}
	
	/**
	 * Checks if FFTW3 binding is available. 
	 * @return yes or no
	 */
	static public boolean isAvailable()
	{
		return available;
	}
}
