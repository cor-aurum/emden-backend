package de.recondita.emden.data.input.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.recondita.emden.data.DataField;

/**
 * Holder Class for a combination of Datafields, a SQL Statement and a
 * Databseconnection
 * 
 * @author felix
 *
 */
public class SQLDataField {
	private DatabaseHolder holder;
	private String[] datafields;
	private String sqlStatement;
	private ResultSet result;
	private Connection connection;

	/**
	 * Constructs a SQLDataField
	 * 
	 * @param holder
	 *            DatabaseHolder
	 * @param datafields
	 *            Array of Datafields, handled by this SQLDataField (Subset of
	 *            Datafields provided in @DataFieldSetup)
	 * @param sqlStatement
	 *            Statement to execute on the Database
	 */
	public SQLDataField(DatabaseHolder holder, String[] datafields, String sqlStatement) {
		this.holder = holder;
		this.datafields = datafields;
		this.sqlStatement = sqlStatement;
	}

	/**
	 * Must be called first to fetch the ResultSet
	 * 
	 * @throws SQLException
	 *             if there is an SQL Error
	 */
	public void init() throws SQLException {
		connection = holder.initDataBase();
		result = connection.createStatement().executeQuery(sqlStatement);
	}

	/**
	 * Gets next Data for the holded Datafields
	 * 
	 * @return Arraylist of Data
	 * @throws SQLException
	 *             If something went wrong
	 */
	public ArrayList<DataField> getNextDatafields() throws SQLException {
		ArrayList<DataField> list = new ArrayList<>();
		for (String df : datafields) {
			String tmp = result.getString(df);
			if (tmp != null && !tmp.isEmpty()) {
				list.add(new DataField(df, tmp));
			}
		}
		return list;
	}

	/**
	 * Calls Next on the ResultSet
	 * 
	 * @return next result available
	 * @throws SQLException
	 *             if something went wrong
	 */
	public boolean next() throws SQLException {
		return result.next();
	}

	/**
	 * Must be called last to close
	 * 
	 * @throws SQLException
	 *             if there is an SQL Error
	 */
	public void close() throws SQLException {
		connection.close();
	}
}
