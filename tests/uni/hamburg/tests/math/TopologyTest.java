package uni.hamburg.tests.math;


import uni.hamburg.yamms.math.Topology;
import junit.framework.TestCase;

public class TopologyTest extends TestCase {
	private Topology t;

	protected void setUp() {
		t = new Topology(
				new double[] {4, 5, 1},
				new int[] {3, 3, 2},
				new int[] {-1, 0, 0});
				
	}

	public void testGetStride() {
		assertEquals(1, t.getStride(0));
		assertEquals(3, t.getStride(1));
		assertEquals(9, t.getStride(2));
	}

	public void testGetCompIdx() {
		int[] components = t.getCompIdx(14);
		assertEquals(1, components[0]);
		assertEquals(1, components[1]);
		assertEquals(1, components[2]);
	}

	public void testGetLinearIdx() {
		int[] components = {1, 2, 1};
		assertEquals(17, t.getLinearIdx(components));
	}
	
	public void testHasCidx() {
		assertTrue(t.hasCidx(new int[] {1, 2, 1}));
		assertTrue(t.hasCidx(new int[] {-1, 2, 1}));
		assertFalse(t.hasCidx(new int[] {-2, 2, 2}));
		assertFalse(t.hasCidx(new int[] {-2, 2, 0}));
	}
	
	public void testDimension() {
		assertEquals(3, t.dimension);
	}
	
	public void testTotalCellCount() {
		assertEquals(18, t.totalCellCount);
	}
	
	public void testGetDistanceTopology() {
		Topology result = t.getDistanceTopology();
		// test size
		assertEquals(75, result.totalCellCount);
		// test root
		assertEquals(37, result.getLinearIdx(new int[] {0, 0, 0}));
	}
	
	public void testNeighborStrides() {
		t.getNeighborStrides();
	}
}
