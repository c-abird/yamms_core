package uni.hamburg.yamms.solver;

import java.io.File;

import uni.hamburg.yamms.io.IOConfig;
import uni.hamburg.yamms.solver.stepHandlers.StepHandler;

/**
 * Specializes the <code>StepHandler</code> interface for handlers that are
 * supposed to store data.
 * 
 * @author Claas Abert
 * 
 */
public abstract class StorageHandler implements StepHandler {

	/**
	 * Checks whether the directory of a file with path <code>path</code> exists
	 * and creates it otherwise
	 * 
	 * @param path
	 *            the path path to a file to be written
	 * @return <code>true</code> on success
	 */
	public static boolean createDirectory(String path) {
		path = IOConfig.getInstance().getPathFor(path);

		String dir = path.substring(0, path.lastIndexOf(IOConfig.SEPARATOR));
		return (new File(dir)).mkdirs();
	}
}
