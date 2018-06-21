package de.recondita.emden.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.recondita.emden.appearance.Result;
import de.recondita.emden.appearance.ResultList;

@Path("/search")
public class SimpleSearchResource {

	@GET
	@Produces("application/json")
	public String getSearchResult(@DefaultValue("42") @QueryParam("query") String query) {
		ResultList l = new ResultList(new Result[] {new Result(query+" Titel1","Text1"), new Result(query+" Titel2","Text2")});
		return l.getJson().toString();
	}
}
