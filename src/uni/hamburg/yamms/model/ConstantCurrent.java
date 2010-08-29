package uni.hamburg.yamms.model;

import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;

/**
 * Temporal constant current field
 * 
 * @author Claas Abert
 * 
 */
public class ConstantCurrent implements Current {

	/** The current field */
	private RealVectorField _current;

	/**
	 * Standard constructor
	 * 
	 * @param current
	 *            the current field
	 */
	public ConstantCurrent(RealVectorField current) {
		_current = current;
	}

	/**
	 * Alternative constructor. Takes a current vector and initializes a
	 * spatially constant current field
	 * 
	 * @param topology
	 *            the topology the field is defined on (should be the topology
	 *            of the magnetization)
	 * @param current
	 *            the current vector
	 */
	public ConstantCurrent(Topology topology, double[] current) {
		_current = RealVectorField.getUniformField(topology, current);
	}

	/* (non-Javadoc)
	 * @see uni.hamburg.m3sc.model.Current#getCurrent(double)
	 */
	public RealVectorField getCurrent(double t) {
		return _current;
	}

}
