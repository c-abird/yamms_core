package uni.hamburg.yamms.fieldTerms;

import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.solver.State;

/**
 * Implementation of a static, uniform zeeman field
 * 
 * @author Claas Abert
 * 
 */
public class StaticZeemanField implements FieldTerm {
	/** the magnetization vector of the uniform field */
	private double[] _fieldVector;

	/** the uniform field */
	private RealVectorField _field;

	/**
	 * Standard constructor. The uniform field is initialized with a field vector
	 * 
	 * @param fieldVector
	 *            the field vector
	 */
	public StaticZeemanField(double[] fieldVector) {
		_fieldVector = fieldVector;
	}

	public RealVectorField calculateField(State state) {
		// initialize field
		if (_field == null || !_field.topology.equals(state.getTopology())) {
			assert state.getM().dimension == _fieldVector.length;
			_field = RealVectorField.getUniformField(state.getTopology(), _fieldVector);
		}
		return _field;
	}

}
