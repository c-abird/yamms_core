package uni.hamburg.tests.fieldTerms.demag;


import java.io.InputStream;

import uni.hamburg.tests.YammsTestCase;
import uni.hamburg.yamms.fieldTerms.DemagField;
import uni.hamburg.yamms.fieldTerms.FieldTerm;
import uni.hamburg.yamms.io.OmfFileService;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.solver.State;

public class DemagFieldTest extends YammsTestCase {
	public void testDemagField() {
		// load magnetization
		InputStream fstream = getClass().getResourceAsStream("m.omf");
		RealVectorField M = OmfFileService.readFile(fstream);
		
		// initialize demag field
		FieldTerm field = new DemagField(M.topology);
		
		// load expected result
		fstream = getClass().getResourceAsStream("heff.omf");
		RealVectorField result = OmfFileService.readFile(fstream);
			
		// calculate result
		RealVectorField heff = field.calculateField(State.getStub(M));
	    
		// check
		assertApprox(result, heff, 1e-6);
	}
	
	

}
