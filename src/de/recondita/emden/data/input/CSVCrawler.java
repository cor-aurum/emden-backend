package de.recondita.emden.data.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.recondita.emden.data.DataField;
import de.recondita.emden.data.DataFieldSetup;
import de.recondita.emden.data.Result;
import de.recondita.emden.data.search.SearchWrapper;

public class CSVCrawler implements CronCrawler {
	private static final long serialVersionUID = 3145532824633805034L;
	private final File csv;
	private final String separator;
	private boolean firstLineHeader;
	private int[] rowsForDatafields;

	/**
	 * Crawlt eine CSV nach bestimmten Daten
	 * 
	 * @param csv
	 * @param separator
	 * @param firstLineHeader
	 * @param rowsForDatafields
	 *            Integer Array, das auf {@code DataFieldSetup.getDatafields()}
	 *            passt. Nimmt -1 für nicht belegte Felder
	 */
	public CSVCrawler(File csv, String separator, boolean firstLineHeader, int[] rowsForDatafields) {
		this.csv = csv;
		this.separator = separator;
		this.firstLineHeader = firstLineHeader;
		this.rowsForDatafields = rowsForDatafields;
	}

	@Override
	public void pushResults(SearchWrapper search) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csv));
			boolean firstLine = true;
			String line = null;
			int succ = 0;
			int nosucc = 0;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty())
					continue;
				String[] data = line.split(separator);
				if (firstLine && firstLineHeader) {
					firstLine = false;
					continue;
				}
				firstLine = false;
				DataField[] dataField = new DataField[data.length];
				int i = 0;
				for (String s : DataFieldSetup.getDatafields()) {
					try {
						if (rowsForDatafields[i] >= 0)
							dataField[i] = new DataField(s, data[rowsForDatafields[i]]);
						i++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (search.pushResult(new Result(dataField)))
					succ++;
				else
					nosucc++;

			}
			System.out.println("Hinzugefügte Dokumente: " + succ);
			System.out.println("Nicht hinzugefügte Dokumente: " + nosucc);
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
		return "CSV";
	}

}
