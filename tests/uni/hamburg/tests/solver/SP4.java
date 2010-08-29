package uni.hamburg.tests.solver;


import java.io.InputStream;
import uni.hamburg.tests.YammsTestCase;
import uni.hamburg.yamms.fieldTerms.*;
import uni.hamburg.yamms.io.*;
import uni.hamburg.yamms.math.*;
import uni.hamburg.yamms.model.*;
import uni.hamburg.yamms.physics.*;
import uni.hamburg.yamms.profiling.Profiler;
import uni.hamburg.yamms.solver.*;
import uni.hamburg.yamms.solver.stepHandlers.StepHandler;

public class SP4 extends YammsTestCase {
	public void testIntegrate() {
		// load initial magnetization
		InputStream fstream = getClass().getResourceAsStream("sp4-groundstate.omf");
		RealVectorField m = OmfFileService.readFile(fstream);
		RealScalarField ms = new RealConstantScalarField(m.topology, 8e5);

		// set up field terms
		CompositeFieldTerm field = new CompositeFieldTerm();
		field.addFieldTerm(new ExchangeField(13e-12, ms));
		field.addFieldTerm(new DemagField(m.topology));
		field.addFieldTerm(new StaticZeemanField(new double[] {
				-24.6e-3 / Constants.MU0, 
                4.3e-3 / Constants.MU0,
                0
        }));
		
		Model model = new BasicModel(2.211e5, 0.02, ms, field);
		StepHandler handler = new StepHandler() {
			protected int counter = 0;
			protected double[][] results = new double[][] {
					new double[] {773373.7511288759,   100596.4373336698,  -0.15959940663834526}, //  0
					new double[] {771128.8674951014,   108169.35920077242, -10010.42340242104},   //  1e-11
					new double[] {764112.3912026784,   129819.59206674535, -19515.776480330365},  //  2e-11
					new double[] {751217.76909631,     163971.71099076208, -28299.369211527108},  //  3e-11
					new double[] {731056.4934460708,   208337.95238554105, -36127.56829078472},   //  4e-11
					new double[] {702459.9701542113,   260087.45506348938, -42837.03689318251},   //  5e-11
					new double[] {664753.0587741518,   316204.4853869893,  -48444.96642558668},   //  6e-11
					new double[] {617676.5163559635,   373859.83069434017, -53204.33171172125},   //  7e-11
					new double[] {561081.8165361932,   430574.7999487876,  -57578.648123568084},  //  8e-11
					new double[] {494560.76406786824,  484096.78276492644, -62185.94475177941},   //  9e-11
					new double[] {417167.547238872,    531985.9436029461,  -67750.9318488139},    // 10e-11
					new double[] {327445.67357938003,  570949.4371501409,  -75016.83944292864},   // 11e-11
					new double[] {224066.22855468217,  596137.4090478382,  -84467.34534012829},   // 12e-11
					new double[] {107330.50049458136,  601286.735435276,   -95792.71409236277},   // 13e-11
					new double[] {-19477.263628830664, 580822.6583580341,  -107660.57908376241}   // 14e-11
			};
			
			public void handleStep(Solver solver, State state) {
                System.out.println("check " + counter + "/14");
				assertApprox(results[counter], state.getM().getAverage(), 10);
				++counter;
			}
		};	

//		AdaptiveStepsizeSolver solver = new DormandPrinceSolver(0, 1e-10, 1, 1e-4);
//		solver.addInterpolatedHandler(handler, 1.0e-11);
		
		Solver solver = new HeunSolver(5e-14, 100, 0);
		solver.addHandler(handler, Condition.everyNthStep(200));

		solver.stopWhen(Condition.timeGreater(14.01e-11));
		solver.integrate(model, m);
		System.out.println(Profiler.getInstance().getSummary());
	}
}
