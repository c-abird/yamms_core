package uni.hamburg.yamms.solver;

import java.util.HashMap;

import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;
import uni.hamburg.yamms.model.Model;

/**
 * Represents a simulation state consisting of:
 * <ul>
 * <li>the magnetization
 * <li>the derivative of the magnetization
 * <li>the simulation time
 * <li>the simulation step
 * <li>a hash map of additional parameters that can be set and altered by event
 * handlers and read by field terms.
 * </ul>
 * 
 * @author Claas Abert
 * 
 */
public class State {
	/**
	 * Returns a stub object with only the magnetization (m) set. This is meant
	 * for use in tests
	 * 
	 * @param m
	 *            the magnetization
	 * @return return state stub
	 */
	public static State getStub(RealVectorField m) {
		return new State(m, null, 0, 0, new HashMap<String, Object>());
	}

	// state attributes
	/** the magnetization */
	protected RealVectorField _m;
	/** the derivative of the magnetization */
	protected RealVectorField _mDot;
	/** the simulation time */
	protected double _t;
	/** the number of the current integration step */
	protected int _step;
	/** a hash map of additional parameters */
	protected HashMap<String, Object> _params;

	// attributes for lazy initialization
	/** the topology of the magnetization */
	protected Topology _topology;
	/**
	 * the values of the magnetization (1 dimensional, used by dormand prince
	 * integrator)
	 */
	protected double[] _valuesM;
	/**
	 * the values of the derivative of the magnetization (1 dimensional, used by
	 * dormand prince integrator)
	 */
	protected double[] _valuesMDot;

	/**
	 * Standard constructor
	 * 
	 * @param params
	 *            hash map of additional parameters
	 */
	public State(HashMap<String, Object> params) {
		_step = -1;
		_params = params;
	}

	/**
	 * Protected constructor used by the <code>derive</code> method.
	 * 
	 * @param m
	 *            the magnetization
	 * @param mDot
	 *            the derivative of the magnetization
	 * @param t
	 *            the simulation time
	 * @param step
	 *            the step number
	 * @param params
	 *            the additional parameters
	 */
	protected State(RealVectorField m, RealVectorField mDot, double t, int step,
			HashMap<String, Object> params) {
		_m = m;
		_mDot = mDot;
		_t = t;
		_step = step;
		_params = params;
	}

	/**
	 * Protected constructor used by the <code>derive</code> method.
	 * 
	 * @param topology
	 *            the topology
	 * @param m
	 *            the values of the magnetization
	 * @param mDot
	 *            the values of the derivative of the magnetization
	 * @param t
	 *            the simulation time
	 * @param step
	 *            the step number
	 * @param params
	 *            the additional parameters
	 */
	protected State(Topology topology, double[] m, double[] mDot, double t, int step,
			HashMap<String, Object> params) {
		this(null, null, t, step, params);
		_topology = topology;
		_valuesM = m;
		_valuesMDot = mDot;
	}

	/**
	 * Derives a state from the current state. (All attributes stay the same
	 * except the magnetization the derivative and the simulation time)
	 * 
	 * @param m
	 *            the new magnetization values
	 * @param mDot
	 *            the new derivative values
	 * @param t
	 *            the new simulation time
	 * @return the derived state
	 */
	public State derive(double[] m, double[] mDot, double t) {
		return new State(_topology, m, mDot, t, _step, _params);
	}

	/**
	 * 
	 * Derives a state from the current state. (All attributes stay the same
	 * except the magnetization the derivative and the simulation time)
	 * 
	 * @param m
	 *            the new magnetization
	 * @param mDot
	 *            the new derivative
	 * @param t
	 *            the new simulation time
	 * @return the derived state
	 */
	public State derive(RealVectorField m, RealVectorField mDot, double t) {
		return new State(m, mDot, t, _step, _params);
	}

	/**
	 * Returns an additional parameter
	 * 
	 * @param key
	 *            the key (name) of the parameter
	 * @return the value of the parameter
	 */
	public Object get(String key) {
		return _params.get(key);
	}

	/**
	 * Returns the lazy initialized (in case of the dormand prince integrator)
	 * field of the magnetization derivative
	 * 
	 * @return the derivative as field
	 */
	public RealVectorField getMDot() {
		if (_mDot == null) {
			_mDot = new RealVectorField(_topology, _valuesMDot);
		}
		return _mDot;
	}

	/**
	 * Returns an additional parameter casted to int
	 * 
	 * @param key
	 *            the key (name) of the parameter
	 * @return the value of the parameter
	 */
	public int getInt(String key) {
		return (Integer) _params.get(key);
	}

	/**
	 * Returns an additional parameter casted to string
	 * 
	 * @param key
	 *            the key (name) of the parameter
	 * @return the value of the parameter
	 */
	public String getString(String key) {
		return (String) _params.get(key);
	}

	/**
	 * Returns an additional parameter casted to double
	 * 
	 * @param key
	 *            the key (name) of the parameter
	 * @return the value of the parameter
	 */
	public double getDouble(String key) {
		return (Double) _params.get(key);
	}

	/**
	 * Returns the lazy initialized (in case of the dormand prince integrator)
	 * field of the magnetization.
	 * 
	 * @return the magnetization field
	 */
	public RealVectorField getM() {
		if (_m == null) {
			_m = new RealVectorField(_topology, _valuesM);
		}
		return _m;
	}

	/**
	 * Returns the current step number
	 * 
	 * @return the step number
	 */
	public int getStep() {
		return _step;
	}

	/**
	 * Return the current simulation time
	 * 
	 * @return the simulation time
	 */
	public double getTime() {
		return _t;
	}

	/**
	 * Returns the topology
	 * 
	 * @return the topology
	 */
	public Topology getTopology() {
		if (_topology == null) _topology = _m.topology;
		return _topology;
	}

	/**
	 * Increases an additional parameter (The parameter has to be an
	 * <code>int</code>)
	 * 
	 * @param key
	 *            the key (name) of the parameter
	 */
	public void incInt(String key) {
		_params.put(key, getInt(key) + 1);
	}

	/**
	 * Sets an additional parameter to a given int value
	 * 
	 * @param key
	 *            the key (name) of the parameter
	 * @param value
	 *            the value
	 */
	public void setInt(String key, int value) {
		_params.put(key, value);
	}

	/**
	 * Sets an additional parameter to a given string value
	 * 
	 * @param key
	 *            the key (name) of the parameter
	 * @param value
	 *            the value
	 */
	public void setString(String key, String value) {
		_params.put(key, value);
	}

	/**
	 * Sets an additional parameter to a given double value
	 * 
	 * @param key
	 *            the key (name) of the parameter
	 * @param value
	 *            the value
	 */
	public void setDouble(String key, double value) {
		_params.put(key, value);
	}

	/**
	 * Sets the topology
	 * 
	 * @param topology
	 *            the topology
	 * @deprecated
	 */
	public void setTopology(Topology topology) {
		_topology = topology;
	}

	/**
	 * Submits a step (the combination of magnetization, derivative and
	 * simulation time). The number of steps if increased.
	 * 
	 * @param m
	 *            the magnetization values
	 * @param mDot
	 *            the derivative of the magnetization values
	 * @param t
	 *            the simulation time
	 */
	public void step(double[] m, double[] mDot, double t) {
		assert _topology != null;

		_valuesM = m;
		_valuesMDot = mDot;
		_t = t;
		_m = null;
		_mDot = null;
		_step++;
	}

	/**
	 * Submits a step (the combination of magnetization, derivative and
	 * simulation time). The number of steps if increased.
	 * 
	 * @param m
	 *            the magnetization
	 * @param mDot
	 *            the derivative of the magnetization
	 * @param t
	 *            the simulation time
	 */
	public void step(RealVectorField m, RealVectorField mDot, double t) {
		_t = t;
		_m = m;
		_mDot = mDot;
		_step++;
	}

	/**
	 * Init method for dormand prince intergrator. the derivative for t=0 is
	 * calculated for the first call of handlers
	 * 
	 * @param model
	 *            the model
	 * @param m0
	 *            the initial magnetization
	 */
	public void init(Model model, RealVectorField m0) {
		_step = 0;
		_t = 0;
		_m = m0;
		_topology = _m.topology;
		_mDot = model.calculateDerivative(this);
	}
}
