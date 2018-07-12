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

/**
 * REST Endpoint for Basic Information
 * @author felix
 *
 */
@Path("/company")
public class CompanyResource {
	
	/**
	 * Logo Endpoint
	 * @return logo
	 */
	@Path("/logo")
	@GET
	@Produces("image/png")
	public Response getLogo() {
		File file = PathProvider.getInstance().getLogo();
		ResponseBuilder resp =Response.ok(file);
		//resp.header("Content-Disposition", "attachment;filename=logo.png");
		return resp.build();
	}
	
	
	/**
	 * Name of the Company
	 * @return name of the company
	 */
	@Path("/name")
	@GET
	@Produces("text/plain")
	public String getCompanyName() {
		return Settings.getInstance().getProperty("company.name");
	}
	
	/**
	 * Configured DataFields
	 * @return datafields (as in DataFieldSetup)
	 */
	@Path("/datafields")
	@GET
	@Produces("application/json")
	public String getDatafields() {
		return DataFieldSetup.getDatafieldsAsJson().toString();
	}
}
