package de.recondita.emden.data.input.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.recondita.emden.data.DataField;
import de.recondita.emden.data.DataFieldSetup;
import de.recondita.emden.data.Result;
import de.recondita.emden.data.input.CronCrawler;
import de.recondita.emden.data.search.Pusher;
import de.recondita.emden.data.search.SearchWrapper;

/**
 * Crawls a CSV for Data, provided in DataFieldSetup
 * 
 * @author felix
 *
 */
public class CSVCrawler implements CronCrawler {
	private static final long serialVersionUID = 3145532824633805034L;
	private final File csv;
	private final String separator;
	private boolean firstLineHeader;
	private int[] rowsForDatafields;
	private String id;

	/**
	 * Crawlt eine CSV nach bestimmten Daten
	 * 
	 * @param csv
	 *            CSV File
	 * @param separator
	 *            Separator String (,;- etc.)
	 * @param firstLineHeader
	 *            whether the first line should be ignored
	 * @param id
	 *            Identifier of the source
	 * @param rowsForDatafields
	 *            Integer Array, das auf {@code DataFieldSetup.getDatafields()}
	 *            passt. Nimmt -1 für nicht belegte Felder
	 */
	public CSVCrawler(File csv, String separator, boolean firstLineHeader, int[] rowsForDatafields, String id) {
		this.csv = csv;
		this.separator = separator;
		this.firstLineHeader = firstLineHeader;
		this.rowsForDatafields = rowsForDatafields;
		this.id = id;
	}

	@Override
	public void pushResults(SearchWrapper search) {
		System.out.println("CSV Crawler");
		long aktTime = System.currentTimeMillis();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csv));
			boolean firstLine = true;
			String line = null;

			// Declare Reusable Objects here, for Performance Reasons
			String[] d = DataFieldSetup.getDatafields();
			String[] data;
			DataField[] dataField = new DataField[d.length];
			int i;
			Pusher pusher= search.pushResults(id);
			while ((line = br.readLine()) != null) {
				if (line.isEmpty())
					continue;
				data = line.split(separator);
				if (firstLine && firstLineHeader) {
					firstLine = false;
					continue;
				}
				firstLine = false;
				// dataField = new DataField[data.length];
				i = 0;
				for (String s : d) {
					try {
						if (rowsForDatafields[i] >= 0)
							dataField[i] = new DataField(s, data[rowsForDatafields[i]]);
						i++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				pusher.writeJsonString(new Result(dataField).getData().toString());
			}
			pusher.send();
			System.out.println("Benötigte Zeit: " + ((System.currentTimeMillis() - aktTime) / 1000) + "s");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getType() {
		return id;
	}

}
