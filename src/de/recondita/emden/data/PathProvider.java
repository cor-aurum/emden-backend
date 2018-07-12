package de.recondita.emden.data;

import java.io.File;

/**
 * Provides FileSystem Paths
 * 
 * @author felix
 *
 */
public final class PathProvider {

	private File logo;
	private File properties;
	private File config;

	/**
	 * Holder for the Singleton
	 * 
	 * @author felix
	 *
	 */
	private static final class InstanceHolder {
		final static PathProvider INSTANCE = new PathProvider();
	}

	/**
	 * Private Constructor to use from the InstanceHolder
	 */
	private PathProvider() {
		String root = System.getenv("EMDEN");
		if (root == null || root.isEmpty())
			throw new RuntimeException("no config dir found. Please set $EMDEN");
		logo = new File(root + File.separator + "res" + File.separator + "logo.png");
		properties = new File(root + File.separator + "config" + File.separator + "settings.properties");
		config = new File(root + File.separator + "config" + File.separator + "config.xml");
	}

	/**
	 * Gives the Singleton-Instance
	 * 
	 * @return instance
	 */
	public static PathProvider getInstance() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * Logopath
	 * 
	 * @return logopath
	 */
	public File getLogo() {
		return logo;
	}

	/**
	 * Properties Path
	 * 
	 * @return properties
	 */
	public File getProperties() {
		return properties;
	}

	/**
	 * Configpath
	 * 
	 * @return configpath
	 */
	public File getConfig() {
		return config;
	}
}
