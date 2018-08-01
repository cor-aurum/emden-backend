package de.recondita.emden.data.input.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Needed to Wrap the DatabaseDriver. To be used in a Drivermanager which only
 * allows Drivers loaded by the same Classloader as it did. Yep. Nasty.
 * 
 * @author felix
 *
 */
public class DriverWrapper implements Driver {
	private Driver driver;

	/**
	 * Constructor
	 * 
	 * @param driver
	 *            "real" driver
	 */
	public DriverWrapper(Driver driver) {
		this.driver = driver;
	}

	@Override
	public boolean acceptsURL(String u) throws SQLException {
		return this.driver.acceptsURL(u);
	}

	@Override
	public Connection connect(String u, Properties p) throws SQLException {
		return this.driver.connect(u, p);
	}

	@Override
	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
		return this.driver.getPropertyInfo(u, p);
	}

	@Override
	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.driver.getParentLogger();
	}
}
