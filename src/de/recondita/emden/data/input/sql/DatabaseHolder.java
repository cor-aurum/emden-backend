package de.recondita.emden.data.input.sql;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class to connect to a Database
 * 
 * @author felix
 *
 */
public class DatabaseHolder {

	private String jdbcDriverPath;
	private String driver;
	private String url;
	private String username;
	private String password;

	/**
	 * Constructor
	 * 
	 * @param jdbcDriverPath
	 *            Path to the JDBC jar file
	 * @param driver
	 *            Drivername to load with the classloader
	 * @param url
	 *            url of the Database
	 * @param username
	 *            Username
	 * @param password
	 *            Passwort
	 */
	public DatabaseHolder(String jdbcDriverPath, String driver, String url, String username, String password) {
		this.jdbcDriverPath = jdbcDriverPath;
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	/**
	 * Really really nasty hack.. thanks to
	 * https://stackoverflow.com/questions/14478870/dynamically-load-the-jdbc-driver#14479658
	 * 
	 * @return Databaseconnection
	 */
	public Connection initDataBase() {
		try {
			URL u = new URL("file://" + jdbcDriverPath);
			URLClassLoader ucl = new URLClassLoader(new URL[] { u });
			Driver d = (Driver) Class.forName(driver, true, ucl).newInstance();
			DriverManager.registerDriver(new DriverWrapper(d));
			return DriverManager.getConnection(url, username, password);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
