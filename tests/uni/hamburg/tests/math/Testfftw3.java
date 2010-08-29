package uni.hamburg.tests.math;

import uni.hamburg.tests.YammsTestCase;
import uni.hamburg.yamms.math.fft.*;

public class Testfftw3 extends YammsTestCase
{

	/**
	 * @param args
	 */
	public static void testFFT()
	{
		if (!FFT_fftw3Impl.isAvailable()) return; // nothing to check..

		int sx = 100;
		int sy = 100;
		
		double []in = new double[2*sx*sy];
		for (int i=0; i<in.length; ++i) in[i] = i;
		
		for (double v: in) {
			System.out.println(v);
		}
		
		FFT fft = new FFT_fftw3Impl(new Spec(Type.FORW_C2C, sx, sy));
		fft.transform(in, in);
		
		FFT ifft = new FFT_fftw3Impl(new Spec(Type.BACK_C2C, sx, sy));
		ifft.transform(in, in);
		
		for (int i=0; i<in.length; ++i) {
			assertApprox(in[i], i * sx * sy);
		}
	}
	
}
