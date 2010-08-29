package uni.hamburg.yamms.solver.stepHandlers;

import uni.hamburg.yamms.io.IOConfig;
import uni.hamburg.yamms.io.OmfFileService;
import uni.hamburg.yamms.solver.Solver;
import uni.hamburg.yamms.solver.State;
import uni.hamburg.yamms.solver.StorageHandler;

/**
 * Standard storage handler for magnetization snapshots in the OMF format (the
 * format used by the OOMMF micromagnetic solver)
 * <p>
 * The files are written in a defined directory. They are numbered consecutively
 * and have a configurable prefix. By now only the ASCII format is supported
 * 
 * @author Claas Abert
 * 
 */
public class OmfStorageHandler extends StorageHandler {

	/** the directory the handler writer the OMF file to */
	private String _directory;
	/** the prefix for the files */
	private String _prefix;
	/**
	 * if <code>false</code> a litte status is written to stdout at every
	 * invocation
	 */
	private boolean _quiet;
	/** the counter for the naming of the files */
	private int _counter;

	/**
	 * Standard constructor
	 * 
	 * @param directory
	 *            the directory the files are stored to
	 * @param prefix
	 *            the prefix of the OMF files
	 * @param quiet
	 *            if <code>false</code> a litte status is written to stdout at
	 *            every invocation
	 */
	public OmfStorageHandler(String directory, String prefix, boolean quiet) {
		_directory = directory;
		// add separator at the end if necessary
		if (_directory.charAt(_directory.length() - 1) != IOConfig.SEPARATOR)
			_directory += IOConfig.SEPARATOR;

		createDirectory(_directory);

		_prefix = prefix;
		_quiet = quiet;
		_counter = 0;
	}

	/**
	 * Alternative constructor. The <code>quiet</code> is set to
	 * <code>false</code>
	 * 
	 * @param directory
	 *            the prefix of the OMF files
	 * @param prefix
	 *            if <code>false</code> a litte status is written to stdout at
	 *            every invocation
	 */
	public OmfStorageHandler(String directory, String prefix) {
		this(directory, prefix, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uni.hamburg.m3sc.integrator.StepHandler#handleStep(uni.hamburg.m3sc.math
	 * .RealVectorField, uni.hamburg.m3sc.math.RealVectorField, double)
	 */
	public void handleStep(Solver solver, State state) {
		// print message
		if (!_quiet) {
			System.out.println(String.format("Write Omf file (thread id: %d; t: %1.4f ns).", 
					Thread.currentThread().getId(), state.getTime() * 1e9));
		}

		// write file
		_counter++;
		String title = "Yamms Omf Storage Handler";
		String[] desc = new String[]{
			String.format(" Total simulation time: %e s", state.getTime())
		};
		OmfFileService.writeFile(state.getM(), getPathForStep(), title, desc);
	}

	/**
	 * Composes the path of the OMF file from directory, prefix and counter
	 * 
	 * @return the path to the OMF file to write
	 */
	private String getPathForStep() {
		return _directory + _prefix + String.format("%06d", _counter) + ".omf";
	}
}
