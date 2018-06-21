package de.recondita.emden.resources;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/logo")
public class LogoResource {
	@GET
	@Produces("image/png")
	public Response getSearchResult() {
		File file = new File("/home/felix/git/suche-backend/WebContent/recondita.png");
		ResponseBuilder resp =Response.ok(file);
		//resp.header("Content-Disposition", "attachment;filename=logo.png");
		return resp.build();
	}
}
