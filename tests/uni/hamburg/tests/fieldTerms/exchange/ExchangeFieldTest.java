package uni.hamburg.tests.fieldTerms.exchange;

import java.io.InputStream;

import uni.hamburg.tests.YammsTestCase;
import uni.hamburg.yamms.fieldTerms.ExchangeField;
import uni.hamburg.yamms.io.OmfFileService;
import uni.hamburg.yamms.math.RealScalarField;
import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.solver.State;

public class ExchangeFieldTest extends YammsTestCase {

	public void testExchangeField() {
		// load magnetization
		InputStream fstream = getClass().getResourceAsStream("m.omf");
		RealVectorField M = OmfFileService.readFile(fstream);
		
		// initialize exchange field
		RealScalarField ms = RealScalarField.getUniformField(M.topology, 8e5);
		ExchangeField field = new ExchangeField(13e-12, ms);
		
		// load expected result
		fstream = getClass().getResourceAsStream("heff.omf");
		RealVectorField result = OmfFileService.readFile(fstream);
			
		// calculate result
		RealVectorField heff = field.calculateField(State.getStub(M));
		
		// check
		assertApprox(result, heff, 1e-9);
	}
}
