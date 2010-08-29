package uni.hamburg.tests.math;

import uni.hamburg.tests.YammsTestCase;
import uni.hamburg.yamms.math.BooleanConstantField;
import uni.hamburg.yamms.math.BooleanField;
import uni.hamburg.yamms.math.Field;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;

public class RealVectorFieldTest extends YammsTestCase {
	private RealVectorField vf1;
	private RealVectorField vf2;
	private RealVectorField vf3;
	private Topology topology;
	private Topology topology2;
	
	public void setUp() {
		topology = new Topology(new double[] {1, 1, 1}, new int[] {2, 2, 1});
		vf1 = new RealVectorField(topology, new double[][] {
				new double[] {1, 2, 3, 4},
				new double[] {5, 6, 7, 8},
				new double[] {3, 4, 5, 6}
		});	
		
		vf2 = new RealVectorField(topology, new double[][] {
				new double[] {2, 3, 4, 5},
				new double[] {6, 7, 8, 9},
				new double[] {1, 2, 3, 4}
		});
		
		topology2 = new Topology(new double[] {1, 1, 1}, new int[] {4, 4, 1});
		vf3 = new RealVectorField(topology2, new double[][] {
			new double[] {1, 4, 5, 7, 3, 2, 5, 6, 5, 8, 7, 9, 8, 9, 8, 5},
			new double[] {5, 7, 5, 3, 7, 9, 6, 5, 7, 1, 5, 7, 6, 1, 5, 4},
			new double[] {7, 8, 7, 5, 8, 5, 2, 6, 5, 7, 6, 7, 8, 8, 8, 8}
		});
	}
	
	public void testAdd() {
		double[][] result = new double[][] {
				new double[] { 3,  5,  7,  9},
				new double[] {11, 13, 15, 17},
				new double[] { 4,  6,  8, 10}
		};
		Field vf = new RealVectorField(topology, result);
		assertApprox(vf, vf1.add(vf2));
	}
	
	public void testScalarTimes() {
		double[][] result = new double[][] {
				new double[] { 2,  4,  6,  8},
				new double[] {10, 12, 14, 16},
				new double[] { 6,  8, 10, 12}
		};
		Field vf = new RealVectorField(topology, result);
		assertApprox(vf, vf1.times(2));
	}
	
	public void testFieldCross() {
		double[][] result = new double[][] {
				new double[] {-13, -16, -19, -22},
				new double[] {  5,   8,  11,  14},
				new double[] { -4,  -4,  -4,  -4}
		};
		Field vf = new RealVectorField(topology, result);
		assertApprox(vf, vf1.cross(vf2));
	}
	
	public void testApplyTopology() {
		Topology t = new Topology(new int[] {3, 2, 1}, new double[] {1, 1, 1});
		double[][] result = new double[][] {
				new double[] {1, 2, 0, 3, 4, 0},
				new double[] {5, 6, 0, 7, 8, 0},
				new double[] {3, 4, 0, 5, 6, 0}
		};
		Field vf = new RealVectorField(t, result);
		assertApprox(vf, vf1.applyTopology(t));
	}
	
	// TODO move to testField
	public void testEquals() {
		assertFalse(vf1.equals(vf2));
		Field vf = new RealVectorField(vf1.topology, new double[][] {
				new double[] {1, 2, 3, 4},
				new double[] {5, 6, 7, 8},
				new double[] {3, 4, 5, 6}
		});
		assertApprox(vf, vf1);
	}
	
	public void testLock() {
		vf1.lock();
		assertTrue(vf1.isLocked());
		
		// out of place operation should still be possible
		vf1.add(vf2);
	}
	
	public void testFirstDerivative() {
		RealVectorField vf = new RealVectorField(vf3.topology, new double[][] {
			new double[] {12, 8,  6,  8,  -4,   4,  8,  4,  12,  4,  2, 8,   4,  0, -8, -12},
			new double[] { 8, 0, -8, -8,   8,  -2, -8, -4, -24, -4, 12, 8, -20, -2,  6, -4},
			new double[] { 4, 0, -6, -8, -12, -12,  2, 16,   8,  2,  0, 4,   0,  0,  0,  0}
		});
		assertApprox(vf, vf3.firstDerivative(0));
	}
	
	public void testSecondDerivative() {
		RealVectorField vf = new RealVectorField(vf3.topology, new double[][] {
				new double[] {80, -64, 16, -48, -16, 192, 0, 48, 64, -144, 32, -144, -32, -48, -48, 112},
				new double[] {64, -32, 16, 64, 0, -240, 0, 16, -112, 288, -16, -112, -64, 144, -80, 64},
				new double[] {32, -80, -96, 48, -112, 80, 256, -64, 128, -64, 0, -16, -48, -16, -32, -16}
		});
		BooleanField bounds = new BooleanConstantField(vf3.topology, true);
		assertApprox(vf, vf3.laplaceWithBounds(bounds));
	}
	
	public void testAverageNorm() {
		assertApprox(8.291561, vf1.getAverageNorm());
	}
	
	public void testAverage() {
		assertApprox(new double[] {2.5, 6.5, 4.5}, vf1.getAverage());
	}
	
	public void testMaxNorm() {
		assertApprox(10.77032, vf1.getMaxNorm());
	}
}
