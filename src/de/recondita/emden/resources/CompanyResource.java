package de.recondita.emden.resources;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import de.recondita.emden.data.DataFieldSetup;
import de.recondita.emden.data.PathProvider;
import de.recondita.emden.data.Settings;

@Path("/company")
public class CompanyResource {
	
	@Path("/logo")
	@GET
	@Produces("image/png")
	public Response getLogo() {
		File file = PathProvider.getInstance().getLogo();
		ResponseBuilder resp =Response.ok(file);
		//resp.header("Content-Disposition", "attachment;filename=logo.png");
		return resp.build();
	}
	
	
	@Path("/name")
	@GET
	@Produces("text/plain")
	public String getCompanyName() {
		return Settings.getInstance().getProperty("company.name");
	}
	
	@Path("/datafields")
	@GET
	@Produces("application/json")
	public String getDatafields() {
		return DataFieldSetup.getDatafieldsAsJson().toString();
	}
}
