package de.recondita.emden.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	private static final class InstanceHolder {
		final static Settings INSTANCE = new Settings();
	}
	
	private Properties properties;
	private Properties defaults;

	private Settings() {
		defaults=new Properties();
		defaults.setProperty("elasticsearch.url", "http://localhost:9200");
		defaults.setProperty("max.searchresults", "25");
		properties=new Properties(defaults);
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
	
	public String getProperty(String key) {
		return properties.getProperty(key, "");
	}
	
	
	public static Settings getInstance() {
		return InstanceHolder.INSTANCE;
	}
}
