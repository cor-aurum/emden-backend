package de.recondita.emden.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.recondita.emden.data.ResultList;
import de.recondita.emden.data.search.ElasticsearchWrapper;

@Path("/search")
public class SimpleSearchResource {

	@GET
	@Produces("application/json")
	public String getSearchResult(@DefaultValue("42") @QueryParam("query") String query) {
		ResultList l = new ElasticsearchWrapper().simpleSearch(query);
		return l.getJson().toString();
	}
}
