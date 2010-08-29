package uni.hamburg.yamms.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Stack;

import uni.hamburg.yamms.math.RealVectorField;
import uni.hamburg.yamms.math.Topology;

/**
 * Service for reading and writing vector fields from/to a file The file format
 * used is the ASCII OMF format defined by the OOMMF simulation framework.
 * 
 * @author Claas Abert
 * 
 */
public class OmfFileService {
	/**
	 * The mode of the file parser, can be one of:
	 * <code>NONE, SEGMENT, HEADER, DATA</code>
	 */
	private static enum Mode {
		NONE, SEGMENT, HEADER, DATA
	};

	/**
	 * Reads a file and returns a <code>RealVectorField</code> Object
	 * 
	 * @param path
	 *            the path to the OMF file
	 * @return the vector field
	 */
	static public RealVectorField readFile(String path) {
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(IOConfig.getInstance().getPathFor(path));
			return readFile(fstream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Reads a file and returns a <code>RealVectorField</code> Object
	 * 
	 * @param fstream
	 *            the input stream of the OMF file
	 * @return the vector field
	 */
	static public RealVectorField readFile(InputStream fstream) {
		// TODO add support for multiple segments
		// TODO add format validations
		try {
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			// values
			String line = null;
			String key = null;
			String value = null;

			// initialize segment
			OmfSegment segment = null;

			// initialize mode stack
			Stack<Mode> modeStack = new Stack<Mode>();
			modeStack.push(Mode.NONE);

			while ((line = br.readLine()) != null) {
				if (line.charAt(0) == '#') {
					// parse key value pair
					int sep = line.indexOf(':');
					key = line.substring(1, sep).trim();
					value = line.substring(sep + 1).trim();

					// handle begin statements
					if (key.equals("Begin")) {
						modeStack.push(getModes().get(value));
						if (modeStack.peek() == Mode.SEGMENT) segment = new OmfSegment();
						continue;

						// handle end statements
					} else if (key.equals("End")) {
						modeStack.pop();
						continue;
					}
				}

				switch (modeStack.peek()) {
				case HEADER:
					segment.addHeader(key, value);
					break;
				case DATA:
					segment.addData(line);
					break;
				default:
					// nothing
				}
			}

			in.close();

			return segment.getVectorField();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Helper function for <code>readFile</code>. Maps the String
	 * representations of the parse mode to the enum representation.
	 * 
	 * @return the hash map with the mapping
	 */
	static private HashMap<String, Mode> getModes() {
		HashMap<String, Mode> sections = new HashMap<String, Mode>();
		sections.put("Segment", Mode.SEGMENT);
		sections.put("Header", Mode.HEADER);
		sections.put("Data Text", Mode.DATA);

		return sections;
	}

	/**
	 * Writes a <code>RealVectorField</code> in an OMF file
	 * 
	 * @param field
	 *            the vector field to be written
	 * @param path
	 *            the path of the target file
	 * @param title
	 *            the title to be written in the header of the file
	 * @param descriptions
	 *            array of description to be written in the header
	 */
	static public void writeFile(RealVectorField field, String path, String title, String[] descriptions) {
		Topology t = field.topology;

		// write
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(IOConfig.getInstance()
					.getPathFor(path)));

			out.write(buildHeaderRow("OOMMF", "rectangular mesh v1.0"));
			out.write(buildHeaderRow("Segment count", 1));

			out.write(buildHeaderRow("Begin", "Segment"));
			out.write(buildHeaderRow("Begin", "Header"));

			out.write(buildHeaderRow("Title", title));
			for (String description : descriptions) {
				out.write(buildHeaderRow("Desc", description));
			}
			out.write(buildHeaderRow("meshunit", "m"));
			out.write(buildHeaderRow("valueunit", "A/m"));
			out.write(buildHeaderRow("valuemultiplier", "1.0"));
			out.write(buildHeaderRow("xmin", t.getOrigin(0) * t.getCellSize(0)));
			out.write(buildHeaderRow("ymin", t.getOrigin(1) * t.getCellSize(1)));
			out.write(buildHeaderRow("zmin", t.getOrigin(2) * t.getCellSize(2)));
			out
					.write(buildHeaderRow("xmax", t.getOrigin(0) + t.getCellCount(0)
							* t.getCellSize(0)));
			out
					.write(buildHeaderRow("ymax", t.getOrigin(1) + t.getCellCount(1)
							* t.getCellSize(1)));
			out
					.write(buildHeaderRow("zmax", t.getOrigin(2) + t.getCellCount(2)
							* t.getCellSize(2)));
			out.write(buildHeaderRow("ValueRangeMaxMag", ""));
			out.write(buildHeaderRow("ValueRangeMinMag", ""));
			out.write(buildHeaderRow("meshtype", "rectangular"));
			out.write(buildHeaderRow("xbase", t.getCellSize(0) / 2));
			out.write(buildHeaderRow("ybase", t.getCellSize(1) / 2));
			out.write(buildHeaderRow("zbase", t.getCellSize(2) / 2));
			out.write(buildHeaderRow("xstepsize", t.getCellSize(0)));
			out.write(buildHeaderRow("ystepsize", t.getCellSize(1)));
			out.write(buildHeaderRow("zstepsize", t.getCellSize(2)));
			out.write(buildHeaderRow("xnodes", t.getCellCount(0)));
			out.write(buildHeaderRow("ynodes", t.getCellCount(1)));
			out.write(buildHeaderRow("znodes", t.getCellCount(2)));

			out.write(buildHeaderRow("End", "Header"));
			out.write(buildHeaderRow("Begin", "Data Text"));

			for (int i = 0; i < field.topology.totalCellCount; i++) {
				out.write(field.getValue(0, i) + " " + field.getValue(1, i) + " "
						+ field.getValue(2, i) + "\n");
			}

			out.write(buildHeaderRow("End", "Data Text"));
			out.write(buildHeaderRow("End", "Segment"));

			out.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Writes a <code>RealVectorField</code> in an OMF file.
	 * 
	 * @param field
	 *            the vector field to be written
	 * @param path
	 *            the path of the target file
	 */
	static public void writeFile(RealVectorField field, String path) {
		writeFile(field, path, "Yamms Omf Storage Service", new String[]{});
	}

	/**
	 * Helper, builds an OMF header row from a key value pair, the value being a
	 * <code>String</code>
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return the row as string
	 */
	static private String buildHeaderRow(String key, String value) {
		return "# " + key + ": " + value + "\n";
	}

	/**
	 * Helper, builds an OMF header row from a key value pair, the value being
	 * an <code>int</code>
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return the row as string
	 */
	static private String buildHeaderRow(String key, int value) {
		return buildHeaderRow(key, Integer.toString(value));
	}

	/**
	 * Helper, builds an OMF header row from a key value pair, the value being a
	 * <code>double</code>
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return the row as string
	 */
	static private String buildHeaderRow(String key, double value) {
		return buildHeaderRow(key, Double.toString(value));
	}
}
