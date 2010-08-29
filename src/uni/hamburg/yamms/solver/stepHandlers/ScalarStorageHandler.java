package uni.hamburg.yamms.solver.stepHandlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import uni.hamburg.yamms.io.IOConfig;
import uni.hamburg.yamms.solver.Solver;
import uni.hamburg.yamms.solver.State;
import uni.hamburg.yamms.solver.StorageHandler;

/**
 * Standard storage handler for scalar parameters of the simulation. The values
 * are stored white space separated in an ASCII file
 * <ul>
 * <li><strong>t</strong>: the simulation time
 * <li><strong>Mx</strong>: the average magnetization in x direction
 * <li><strong>My</strong>: the average magnetization in y direction
 * <li><strong>Mz</strong>: the average magnetization in z direction
 * <li><strong>|M|</strong>: the average norm of the magnetization
 * <li><strong>dM_max</strong>: the maximum derivation of the magnetization at
 * the last step
 * </ul>
 * 
 * @author Claas Abert
 * 
 */
public class ScalarStorageHandler extends StorageHandler {

	/** the path to the file */
	private String _path;
	/**
	 * if <code>false</code> a litte status is written to stdout at every
	 * invocation
	 */
	private boolean _quiet;

	/**
	 * The standard constructor
	 * 
	 * @param path
	 *            the path to the scalar file
	 * @param quiet
	 *            if <code>false</code> a litte status is written to stdout at
	 *            every invocation
	 */
	public ScalarStorageHandler(String path, boolean quiet) {
		_path = path;

		createDirectory(path);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(IOConfig.getInstance()
					.getPathFor(_path), false));
			out.write("# M3Sc Scalar Value Table --- Version 1e-12\n");
			out.write("# step t Mx My Mz |M| dM_dt_max\n");
			out.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
		_quiet = quiet;
	}

	/**
	 * Alternative constructor. THe <code>quite</code> flag is set to false.
	 * 
	 * @param path
	 *            the path to the scalar file
	 */
	public ScalarStorageHandler(String path) {
		this(path, false);
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
			System.out.println(String.format("Write Scalar data (thread id: %d; t: %1.4f ns).",
					Thread.currentThread().getId(), state.getTime() * 1e9));
		}

		// write
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(IOConfig.getInstance()
					.getPathFor(_path), true));
			out.write(getRow(state));
			out.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Returns the row to be written in the ASCII file
	 * 
	 * @param m
	 *            the current magnetization
	 * @param dm
	 *            the current derivative of the magnetization
	 * @param t
	 *            the current integration time
	 * @return the row to be written
	 */
	private String getRow(State state) {

		double[] avgM = state.getM().getAverage();
		String result = "" + state.getStep() + " " + state.getTime() + " " + avgM[0] + " "
				+ avgM[1] + " " + avgM[2] + " " + state.getM().getAverageNorm() + " "
				+ state.getMDot().getMaxNorm() + "\n";

		return result;
	}

}
