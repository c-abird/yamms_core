package uni.hamburg.yamms.io;

/**
 * Singleton that contains the configuration for all file reading and writing
 * classes and offers methods for retrieving path names.
 * 
 * @author Claas Abert
 * 
 */
public class IOConfig {
	/** The OS dependent file separator */
	// public static final char SEPARATOR = '/';
	public static final char SEPARATOR = System.getProperty("file.separator")
			.charAt(0);

	/** The singleton instance */
	private static final IOConfig INSTANCE = new IOConfig();

	/** The base path (the context of all relative paths) */
	private String basePath;

	/**
	 * The private standard constructor. Sets the base path to the current path
	 */
	private IOConfig() {
		setBasePath("." + SEPARATOR);
	}

	/**
	 * Returns the instance of the singleton
	 * 
	 * @return the instance
	 */
	public static IOConfig getInstance() {
		return INSTANCE;
	}

	/**
	 * Sets the base path from which the relative paths are derived
	 * 
	 * @param basePath
	 *            the base path
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
		// add separator if necessary
		if (basePath.charAt(basePath.length() - 1) != SEPARATOR)
			this.basePath += SEPARATOR;
	}

	/**
	 * Returns the base path
	 * 
	 * @return the base path
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * Returns the absolute path for a given path. If the path is relative, the
	 * base path is added
	 * 
	 * @param path
	 *            the path
	 * @return the absolute path
	 */
	public String getPathFor(String path) {
		if (path.charAt(0) == SEPARATOR)
			return path;

		return basePath + path;
	}

}
