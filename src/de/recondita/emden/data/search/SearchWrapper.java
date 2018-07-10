package de.recondita.emden.data.search;

import de.recondita.emden.data.Result;
import de.recondita.emden.data.ResultList;

public interface SearchWrapper {
	ResultList simpleSearch(String query);
	Result getResult(int id);
	void pushResult(Result r);
	ResultList advancedSearch(String[] query);
}
