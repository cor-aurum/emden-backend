package de.recondita.emden.data;

import java.io.File;

public class PathProvider {

	private File logo;
	private File properties;
	private File config;

	private static final class InstanceHolder {
		final static PathProvider INSTANCE = new PathProvider();
	}

	private PathProvider() {
		String root=System.getenv("EMDEN");
		if(root==null || root.isEmpty())
			throw new RuntimeException("no config dir found. Please set $EMDEN");
		logo = new File(root + File.separator + "res" + File.separator + "logo.png");
		properties = new File(root + File.separator + "config" + File.separator + "settings.properties");
		config = new File(root + File.separator + "config" + File.separator + "config.xml");
	}

	public static PathProvider getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public File getLogo() {
		return logo;
	}
	
	public File getProperties() {
		return properties;
	}
	
	public File getConfig() {
		return config;
	}
}
