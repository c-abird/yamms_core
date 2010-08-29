package uni.hamburg.yamms.io;

import java.util.HashMap;

import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;

/**
 * Represents a Segment of an OMF file
 * 
 * @author Claas Abert
 * 
 */
public class OmfSegment {
	/** The header entries as <code>HashMap</code> */
	private HashMap<String, String> _headers;
	/** The field data */
	private double[][] _data;
	/** counter for keeping track of the data line currently processed */
	private int _dataPointer;

	/**
	 * Standard constructor
	 */
	public OmfSegment() {
		_headers = new HashMap<String, String>();
		_dataPointer = 0;
	}

	/**
	 * Adds a header entry (key value pair) to the segment
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void addHeader(String key, String value) {
		String result = new String(value);
		if (_headers.containsKey(key)) {
			result = _headers.get(key) + "\n" + result;
		}
		_headers.put(key, result);
	}

	/**
	 * Parses a data row in OMF format and writes it to the data array
	 * 
	 * @param line
	 *            the row
	 */
	public void addData(String line) {
		// TODO check dimensions
		if (_dataPointer == 0)
			initDataContainer();

		String[] values = line.split(" ");
		for (int i = 0; i < values.length; i++) {
			_data[i][_dataPointer] = Double.parseDouble(values[i]);
		}
		_dataPointer++;
	}

	/**
	 * Initializes the data array according to the header information (size of
	 * the field)
	 */
	public void initDataContainer() {
		int cellCount = 1;
		cellCount *= Integer.parseInt(_headers.get("xnodes"));
		cellCount *= Integer.parseInt(_headers.get("ynodes"));
		cellCount *= Integer.parseInt(_headers.get("znodes"));

		_data = new double[3][cellCount];
		_dataPointer = 0;
	}

	/**
	 * Sets up a <code>Topology</code> and <code>RealVectorField</code> with the
	 * parsed data and returns it
	 * 
	 * @return the vector field
	 */
	public RealVectorField getVectorField() {
		Topology t = new Topology(new int[] {
				Integer.parseInt(_headers.get("xnodes")),
				Integer.parseInt(_headers.get("ynodes")),
				Integer.parseInt(_headers.get("znodes")) }, new double[] {
				Double.parseDouble(_headers.get("xstepsize")),
				Double.parseDouble(_headers.get("ystepsize")),
				Double.parseDouble(_headers.get("zstepsize")) });

		return new RealVectorField(t, _data);
	}
}
