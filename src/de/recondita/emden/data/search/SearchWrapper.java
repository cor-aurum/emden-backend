package de.recondita.emden.data.search;

import de.recondita.emden.data.Result;
import de.recondita.emden.data.ResultList;

public interface SearchWrapper {
	ResultList simpleSearch(String query);
	boolean pushResult(Result r);
	ResultList advancedSearch(String[] query, boolean exact);
}
