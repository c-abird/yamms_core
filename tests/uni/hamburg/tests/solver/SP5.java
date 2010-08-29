package uni.hamburg.tests.solver;


import java.io.InputStream;

import uni.hamburg.tests.YammsTestCase;
import uni.hamburg.yamms.fieldTerms.*;
import uni.hamburg.yamms.io.*;
import uni.hamburg.yamms.math.*;
import uni.hamburg.yamms.model.*;
import uni.hamburg.yamms.solver.*;
import uni.hamburg.yamms.solver.stepHandlers.StepHandler;

public class SP5 extends YammsTestCase {
	public void testIntegrate() {
		// load initial magnetization
		InputStream fstream = getClass().getResourceAsStream("sp5-groundstate.omf");
		RealVectorField m = OmfFileService.readFile(fstream);
		RealScalarField ms = new RealConstantScalarField(m.topology, 8e5);

		// set up field terms
		CompositeFieldTerm field = new CompositeFieldTerm();
		field.addFieldTerm(new ExchangeField(13e-12, ms));
		field.addFieldTerm(new DemagField(m.topology));
		
		// set up current
		Current current = new ConstantCurrent(m.topology, new double[] {1e12, 0, 0});
		
		Model model = new CurrentModel(2.211e5, 0.1, ms, 0.05, current, field);
		AdaptiveStepsizeSolver solver = new DormandPrinceSolver(1e-14, 1e-10, 1e-10, 1e-5);
		
		StepHandler handler = new StepHandler() {
			protected int counter = 0;
			protected double[][] results = new double[][] {
					new double[] {0.012120691173849627, 0.005556334409024566, 22015.18846431873}, // 0.0e-10
					new double[] {-4501.0166443135895, 40388.52191068226, 21923.90062451947},     // 0.5e-10
					new double[] {-20830.193310547478, 74834.86077223174, 21476.300224887505},    // 1.0e-10
					new double[] {-43333.13066082288, 107894.30216507887, 21179.140602409225},    // 1.5e-10
					new double[] {-70628.65291320602, 129823.6996334005, 20327.09039619632},      // 2.0e-10
					new double[] {-102808.63675093475, 147245.78649078574, 19852.310278237816},   // 2.5e-10
					new double[] {-135065.05500984544, 155146.59854529545, 19083.44665341255},    // 3.0e-10
					new double[] {-167884.7901514193, 155297.8564197053, 18332.654877112076},     // 3.5e-10
					new double[] {-198168.41859307548, 148591.78667436412, 17845.06654192939},    // 4.0e-10
					new double[] {-224644.99555749106, 134540.00905796172, 17186.30084838843},    // 4.5e-10
					new double[] {-246866.28567620408, 115629.8294847852, 16885.16162348505},     // 5.0e-10
					new double[] {-263169.65165089, 92679.73032621299, 16631.605551527788},       // 5.5e-10
					new double[] {-273886.38537329825, 67093.96653905336, 16485.92141426381},     // 6.0e-10
					new double[] {-278569.56740990304, 40755.46111748155, 16578.66513333177},     // 6.5e-10
					new double[] {-277423.32270588045, 14543.562633233088, 16615.173634868715},   // 7.0e-10
					new double[] {-271035.5331005409, -10100.106717466115, 16874.589673291186},   // 7.5e-10
					new double[] {-259807.3957571353, -32058.525346993865, 17136.51957930463},    // 8.0e-10
					new double[] {-244723.5716450416, -50453.04583896435, 17530.507302641676},    // 8.5e-10
					new double[] {-226614.64340176454, -64340.21088215249, 17989.49712915685},    // 9.0e-10
					new double[] {-206599.60948921094, -73254.92167411477, 18488.49719580324},    // 9.5e-10
					new double[] {-185780.99922979486, -76954.40153117702, 19016.59220145364}     // 10.0e-10
			};
			
			public void handleStep(Solver solver, State state) {
                System.out.println("check " + counter + "/20");
				assertApprox(results[counter], state.getM().getAverage(), 1);
				++counter;
			}
		};
		
		solver.addInterpolatedHandler(handler, 5e-11);
		solver.stopWhen(Condition.timeGreater(10.01e-10));
		solver.integrate(model, m);
	}
}
