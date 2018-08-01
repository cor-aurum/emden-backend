package de.recondita.emden.data.search;

import de.recondita.emden.data.Result;
import de.recondita.emden.data.ResultList;

/**
 * Wrapper for Searchengines
 * 
 * @author felix
 *
 */
public interface SearchWrapper {
	/**
	 * Does a Simple Search with a single searchterm
	 * 
	 * @param query
	 *            searchterm
	 * @return ResultList
	 */
	ResultList simpleSearch(String query);

	/**
	 * gets a single Result and Index' it
	 * 
	 * @param r
	 *            Result
	 * @param index
	 *            Index to push on
	 * @return Success
	 */
	boolean pushResult(Result r, String index);

	/**
	 * A bulk push
	 * 
	 * @param index
	 *            index to push on
	 * @return Pusherobject
	 */
	Pusher pushResults(String index);

	/**
	 * More Advanced Search with multiple fields
	 * 
	 * @param query
	 *            StringArray (as definded in DataFieldSetup). Takes an empty String
	 *            for empty Fields
	 * @param exact
	 *            whether the exact Strings need to be found
	 * @return Resultlist
	 */
	ResultList advancedSearch(String[] query, boolean exact);

	/**
	 * Creates a empty Index
	 * 
	 * @param id
	 *            name of the Index
	 */
	void createIndex(String id);
}
