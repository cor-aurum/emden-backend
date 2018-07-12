package de.recondita.emden.data.input;

import java.io.Serializable;

import de.recondita.emden.data.search.SearchWrapper;

public interface CronCrawler extends Serializable {
	public void pushResults(SearchWrapper search);
	
	public String getType();
}
