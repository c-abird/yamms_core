package uni.hamburg.tests.math;

import uni.hamburg.yamms.math.ComplexVectorField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;
import junit.framework.TestCase;

public class ComplexVectorFieldTest extends TestCase {

	public void testToRealVectorField() {
		Topology t = new Topology(new int[] {2, 1, 1}, new double[] {1, 1, 1});
		
		ComplexVectorField vf = new ComplexVectorField(t, new double[][] {
				new double[] {1, 0, 0, 2},
				new double[] {2, 3, 4, 5},
				new double[] {4, 5, 6, 2}
		});
		
		RealVectorField result = new RealVectorField(t, new double[][] {
				new double[] {1, 0},
				new double[] {2, 4},
				new double[] {4, 6}
		});
		
		assertEquals(result, vf.toRealVectorField());
	}

}
