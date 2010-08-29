package uni.hamburg.tests.math;

import java.io.InputStream;

import uni.hamburg.yamms.io.OmfFileService;
import uni.hamburg.yamms.math.InterpolationService;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;
import junit.framework.TestCase;

public class InterpolationServiceTest extends TestCase {
	private RealVectorField vf1;
	private Topology topology;
	
	public void setUp() {
		topology = new Topology(new double[] {1, 1, 1}, new int[] {2, 2, 1});
		vf1 = new RealVectorField(topology, new double[][] {
				new double[] {1, 2, 3, 4},
				new double[] {5, 6, 7, 8},
				new double[] {3, 4, 5, 6}
		});
	}
	
	public void testInterpolate() {
		RealVectorField vf2 = InterpolationService.interpolate(vf1, new int[] {4, 4, 1});
		System.out.println(vf2);
	}
	
	public void testGetMax() {
		InputStream fstream = getClass().getResourceAsStream("vortex.omf");
//		InputStream fstream = getClass().getResourceAsStream("sp5_init.omf");
		RealVectorField M = OmfFileService.readFile(fstream);
//		M = InterpolationService.interpolate(M, new int[] {1000, 1000, 1});
		
		double max[] = InterpolationService.getMaxPosition(M, 2);
		System.out.println(max[0]);
		System.out.println(max[1]);
	}
}
