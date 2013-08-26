/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.repository.rt.RTRepositoryException;
import eu.gloria.gs.services.repository.rt.RTRepositoryInterface;
import eu.gloria.gs.services.repository.rt.data.DeviceInformation;
import eu.gloria.gs.services.repository.rt.data.DeviceType;
import eu.gloria.gs.services.repository.rt.data.ObservatoryInformation;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */
@Path("/observatories")
public class Observatories extends CORSResource {

	@Context
	HttpServletRequest request;

	private static RTRepositoryInterface telescopes;

	static {
		GSClientProvider.setHost("saturno.datsi.fi.upm.es");
		GSClientProvider.setPort("8443");
		telescopes = GSClientProvider.getRTRepositoryClient();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list")
	public Response getObservatories(@QueryParam("detailed") boolean detailed) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			List<String> names = null;
			List<ObservatoryInformation> observatories = new ArrayList<>();
			names = telescopes.getAllObservatoryNames();

			if (detailed) {
				for (String name : names) {
					ObservatoryInformation obsInfo = telescopes
							.getObservatoryInformation(name);

					observatories.add(obsInfo);
				}

				return this.makeCORS(Response.ok(observatories));
			} else {
				return this.makeCORS(Response.ok(names));
			}

		} catch (RTRepositoryException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}")
	public Response getObservatoryInformation(@PathParam("name") String name) {

		name = name.replace("-", " ");

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			ObservatoryInformation obsInfo = telescopes
					.getObservatoryInformation(name);
			return this.makeCORS(Response.ok(obsInfo));

		} catch (RTRepositoryException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/register")
	public Response registerObservatory(@QueryParam("name") String name,
			@QueryParam("country") String country,
			@QueryParam("city") String city) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			telescopes.registerObservatory(name, city, country);
			return this.makeCORS(Response.ok());

		} catch (RTRepositoryException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}/telescopes/list")
	public Response getTelescopesInObservatory(@PathParam("name") String name) {

		name = name.replace("-", " ");

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<String> rtNames = telescopes.getAllRTInObservatory(name);
			return this.makeCORS(Response.ok(rtNames));

		} catch (RTRepositoryException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}/telescopes/add/{rt}")
	public Response addTelescope(@PathParam("name") String name,
			@PathParam("rt") String rt) {

		name = name.replace("-", " ");

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			telescopes.setRTObservatory(rt, name);
			return this.makeCORS(Response.ok());

		} catch (RTRepositoryException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		}
	}
}
