package de.recondita.emden.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Manages the Program Settings
 * 
 * @author felix
 *
 */
public final class Settings {
	/**
	 * SingletonHolder
	 * 
	 * @author felix
	 *
	 */
	private static final class InstanceHolder {
		final static Settings INSTANCE = new Settings();
	}

	private Properties properties;
	private Properties defaults;

	/**
	 * Private Constructor for the SingletonHolder
	 */
	private Settings() {
		defaults = new Properties();
		defaults.setProperty("elasticsearch.url", "http://localhost:9200");
		defaults.setProperty("max.searchresults", "25");
		defaults.setProperty("default.cron", "0 4 * * * ?");
		defaults.setProperty("http.blocksize", "100000");
		defaults.setProperty("index.basename", "emden");
		defaults.setProperty("sql.supportShortlist", "false");
		properties = new Properties(defaults);
		FileInputStream in;
		try {
			in = new FileInputStream(PathProvider.getInstance().getProperties());
			properties.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gives a Property to a given key
	 * 
	 * @param key
	 *            propertykey
	 * @return property
	 */
	public String getProperty(String key) {
		return properties.getProperty(key, "");
	}

	/**
	 * Gives the Insatnce of the Singleton
	 * 
	 * @return Instance
	 */
	public static Settings getInstance() {
		return InstanceHolder.INSTANCE;
	}
}
