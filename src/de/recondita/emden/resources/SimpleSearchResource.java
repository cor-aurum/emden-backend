package de.recondita.emden.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.recondita.emden.data.DataFieldSetup;
import de.recondita.emden.data.ResultList;
import de.recondita.emden.data.search.ElasticsearchWrapper;

/**
 * Endpoint for SearchRequests
 * @author felix
 *
 */
@Path("/search")
public class SimpleSearchResource {

	/**
	 * SimpleSearch
	 * @param query single term Query
	 * @return result (as JSON)
	 */
	public String getSearchResult(String query) {
		ResultList l = new ElasticsearchWrapper().simpleSearch(query);
		return l.getJson().toString();
	}

	/**
	 * Search
	 * @param ui GET Parameters
	 * @return JSON String
	 */
	@GET
	@Produces("application/json")
	public String getAdvancedSearchResult(@Context UriInfo ui) {
		MultivaluedMap<String, String> query = ui.getQueryParameters();
		String[] datafields = DataFieldSetup.getDatafields();
		String[] params = new String[datafields.length];
		int i = 0;
		for (String s : datafields) {
			params[i] = query.getFirst(s) == null ? "" : query.getFirst(s);
			i++;
		}
		boolean simpleSearch = true;
		for (String s : params) {
			if (s != null && !s.isEmpty()) {
				simpleSearch = false;
			}
		}
		if (simpleSearch)
			return getSearchResult(query.getFirst("query"));
		ResultList l = new ElasticsearchWrapper().advancedSearch(params, false);
		return l.getJson().toString();
	}
}
