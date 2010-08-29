package uni.hamburg.yamms.profiling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A simple profiling tool, implemented as a singleton class.
 * 
 * @author Claas Abert
 * 
 */
public class Profiler {
	/**
	 * A inner class representing the collected statistics for a single call
	 * 
	 * @author Claas Abert
	 * 
	 */
	private class Stats {
		/** The number of calls */
		private int _number;
		/** The aggregated time */
		private double _time;
		/** The start time of a single measurement */
		private double _start;

		/**
		 * Standard constructor
		 */
		public Stats() {
			_number = 0;
			_time = 0.0;
		}

		/**
		 * Starts time measurement
		 */
		public void tic() {
			_start = System.nanoTime();
		}

		/**
		 * Stops time measurement, adds the time to the aggregated time a
		 * increases call counter
		 */
		public void toc() {
			_time += System.nanoTime() - _start;
			_number++;
		}

		/**
		 * Returns the average call time
		 * 
		 * @return the average call time
		 */
		public double getAvg() {
			return 1e-6 * _time / _number;
		}

		/**
		 * Returns the number of calls
		 * 
		 * @return the number of calls
		 */
		public int getCalls() {
			return _number;
		}
	}

	/** The instance of the singleton */
	private static final Profiler INSTANCE = new Profiler();
	/** The collection of stats for different calls */
	private HashMap<String, Stats> _data;

	/**
	 * Private Standard constructor
	 */
	private Profiler() {
		_data = new HashMap<String, Stats>();
	};

	/**
	 * Returns the instance of the profiler
	 * 
	 * @return the instance
	 */
	public static Profiler getInstance() {
		return INSTANCE;
	}

	/**
	 * Starts single measurement identified by a certain name
	 * 
	 * @param name
	 *            the name of the measurement
	 */
	public void tic(String name) {
		Stats stats;
		if (!_data.containsKey(name)) {
			stats = new Stats();
			_data.put(name, stats);
		} else {
			stats = _data.get(name);
		}
		stats.tic();
	}

	/**
	 * Stops the measurement with the provided name
	 * 
	 * @param name
	 *            the name
	 */
	public void toc(String name) {
		_data.get(name).toc();
	}

	/**
	 * Returns a summary of all measurements
	 * 
	 * @return a summary
	 */
	public String getSummary() {
		StringBuffer result = new StringBuffer("\n");
		result.append("Profiling Results (Average Execution Time and Number of Calls)\n");
		result.append("==============================================================\n");

		// create sorted list of keys
		List<String> keys = new ArrayList<String>();
		keys.addAll(_data.keySet());
		Collections.sort(keys);

		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			Stats stats = _data.get(name);
			String printName = name.replaceAll("[^.]*\\.", "  ");

			result.append(String.format("%-30s: %10f ms (%d calls)\n", printName, stats.getAvg(),
					stats.getCalls()));
		}

		return result.toString();
	}

	/**
	 * Retrieves the class name of an object and cuts off the package
	 * information eg. uni.hamburg.m3sc.profiler.Profiler gets Profiler
	 * 
	 * @param obj
	 *            the object
	 * @return the simplified classname
	 */
	static public String getSimpleClassName(Object obj) {
		String name = obj.getClass().getName();
		int i = name.lastIndexOf('.');
		return name.substring(i + 1);
	}
}