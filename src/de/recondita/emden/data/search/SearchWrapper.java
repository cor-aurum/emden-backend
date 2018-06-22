package de.recondita.emden.data.search;

import de.recondita.emden.appearance.Result;
import de.recondita.emden.appearance.ResultList;

public interface SearchWrapper {
	ResultList simpleSearch(String query);
	Result getResult(int id);
}
