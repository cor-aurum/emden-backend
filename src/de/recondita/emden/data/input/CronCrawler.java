package de.recondita.emden.data.input;

import de.recondita.emden.data.search.SearchWrapper;

public interface CronCrawler {
	public void pushResults(SearchWrapper search);
}
