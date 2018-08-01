package de.recondita.emden.data.input.sql;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import de.recondita.emden.data.DataField;
import de.recondita.emden.data.Result;
import de.recondita.emden.data.Settings;
import de.recondita.emden.data.input.CronCrawler;
import de.recondita.emden.data.search.Pusher;
import de.recondita.emden.data.search.SearchWrapper;

/**
 * Crawler for a SQL Database
 * 
 * @author felix
 *
 */
public class JDBCCrawler implements CronCrawler {
	private static final long serialVersionUID = 6156504647993512451L;

	private String id;
	private SQLDataField[] sql;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Id of the Index
	 * @param sql
	 *            Array of SQL Sources
	 */
	public JDBCCrawler(String id, SQLDataField[] sql) {
		this.id = id;
		this.sql = sql;
	}

	@Override
	public void pushResults(SearchWrapper search) {
		Pusher pusher = search.pushResults(id);
		try {
			for (SQLDataField df : sql) {
				df.init();
			}
			while (allNext()) {
				ArrayList<DataField> list = new ArrayList<DataField>();
				for (SQLDataField df : sql) {
					list.addAll(df.getNextDatafields());
				}
				pusher.writeJsonString(new Result(list.toArray(new DataField[list.size()])).getData().toString());
			}
			pusher.send();
			for (SQLDataField df : sql) {
				df.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean allNext() throws SQLException {
		String shortList = Settings.getInstance().getProperty("sql.supportShortlist");
		if (shortList.toLowerCase().startsWith("f")) {
			for (SQLDataField df : sql) {
				if (!df.next())
					return false;
			}
			return true;
		}
		boolean ret = false;
		for (SQLDataField df : sql) {
			if (df.next())
				ret = true;
		}
		return ret;
	}

	@Override
	public String getType() {
		return id;
	}

}
