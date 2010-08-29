package uni.hamburg.tests.math;

import uni.hamburg.yamms.math.ComplexTensorField;
import uni.hamburg.yamms.math.ComplexVectorField;
import uni.hamburg.yamms.math.Topology;
import junit.framework.TestCase;

public class ComplexTensorFieldTest extends TestCase {
	public void testTime() {
		Topology t = new Topology(new int[] {2, 1, 1}, new double[] {1, 1, 1});
		
		ComplexVectorField vf = new ComplexVectorField(t, new double[][] {
				new double[] {1, 0, 0, 2},
				new double[] {2, 3, 4, 5},
				new double[] {4, 5, 6, 2}
		});
		
		ComplexTensorField tf = new ComplexTensorField(t, new double[][] {
				new double[] {3, 4, 5, 6},
				new double[] {3, 5, 6, 7},
				new double[] {5, 0, 0, 8},
				new double[] {1, 2, 3, 4},
				new double[] {5, 7, 3, 8},
				new double[] {2, 4, 4, 5},
				new double[] {1, 1, 3, 3},
				new double[] {4, 4, 2, 4},
				new double[] {6, 6, 6, 8}
		});
		
		ComplexVectorField result = new ComplexVectorField(t, new double[][] {
				new double[] {14, 48, -39, 116},
				new double[] {-22, 57, -22, 91},
				new double[] {-9, 75, 2, 92}
		});
		
		assertEquals(result, tf.times(vf));
	}

}
