package de.recondita.emden.data.input;

import java.io.Serializable;

import de.recondita.emden.data.search.SearchWrapper;

/**
 * Crawler for DataSources to be periodically called
 * @author felix
 *
 */
public interface CronCrawler extends Serializable {

	/**
	 * Pushs the Results to a SearchWrapper (ElasticSearch)
	 * 
	 * @param search
	 *            SearchWrapper to Push to
	 */
	void pushResults(SearchWrapper search);

	/**
	 * Source Identifier
	 * 
	 * @return id
	 */
	String getType();
}
