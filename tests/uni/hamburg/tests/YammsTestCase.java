package uni.hamburg.tests;

import uni.hamburg.yamms.math.Field;
import junit.framework.TestCase;

public class YammsTestCase extends TestCase {

	public static void assertApprox(double expected, double actual, double tolerance) {
		assertEquals(expected, actual, tolerance);
	}

	public static void assertApprox(double expected, double actual) {
		assertApprox(expected, actual, 0.00001);
	}

	public static void assertApprox(double[] expected, double[] actual, double tolerance) {
		assertEquals(expected.length, actual.length);
		for (int i=0; i<expected.length; ++i) {
			assertEquals(expected[i], actual[i], tolerance);
		}
	}

	public static void assertApprox(double[] expected, double[] actual) {
		assertApprox(expected, actual, 0.00001);
	}

	public static void assertApprox(Field expected, Field actual, double tolerance) {
		assertTrue("expected " + expected + ", but was " + actual, expected.approx(actual, tolerance));
	}

	public static void assertApprox(Field expected, Field actual) {
		assertApprox(expected, actual, 0.00001);
	}

}
