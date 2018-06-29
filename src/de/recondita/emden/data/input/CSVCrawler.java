package de.recondita.emden.data.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.recondita.emden.data.DataField;
import de.recondita.emden.data.Result;
import de.recondita.emden.data.search.SearchWrapper;

public class CSVCrawler implements CronCrawler {

	private final File csv;
	private final String separator;
	private boolean firstLineHeader;
	private String[] header;

	public CSVCrawler(File csv, String separator, boolean firstLineHeader, String[] header) {
		this.csv = csv;
		this.separator = separator;
		this.firstLineHeader = firstLineHeader;
		this.header = header;
	}

	@Override
	public void pushResults(SearchWrapper search) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csv));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(separator);

				if (firstLineHeader || header.length != data.length) {
					for (String d : data) {
						d = d.trim();
					}
					header = data;
					firstLineHeader = false;
				} else {
					DataField[] dataField = new DataField[data.length];
					int i = 0;
					for (String d : data) {
						dataField[i] = new DataField(header[i], d.trim());
						i++;
					}
					search.pushResult(new Result(dataField));
				}

			}
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

}
