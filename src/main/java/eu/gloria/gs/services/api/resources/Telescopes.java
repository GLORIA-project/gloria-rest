/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.repository.rt.RTRepositoryException;
import eu.gloria.gs.services.repository.rt.RTRepositoryInterface;
import eu.gloria.gs.services.repository.rt.data.DeviceInformation;
import eu.gloria.gs.services.repository.rt.data.DeviceType;
import eu.gloria.gs.services.repository.rt.data.RTAvailability;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */
@Path("/telescopes")
public class Telescopes {

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
	@Path("/{name}/devices/list")
	public Response getDevices(@PathParam("name") String name,
			@QueryParam("detailed") boolean detailed,
			@QueryParam("type") String type) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<String> names = null;

			if (type == null) {
				names = telescopes.getDeviceNames(name);
			} else {
				names = telescopes.getRTDeviceNames(name,
						DeviceType.valueOf(type));
			}

			if (detailed) {
				List<DeviceInformation> devices = new ArrayList<>();

				for (String device : names) {
					DeviceInformation devInfo = telescopes
							.getRTDeviceInformation(name, device);
					devices.add(devInfo);
				}

				return Response.ok(devices).build();
			} else {
				return Response.ok(names).build();
			}

		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}/devices/{device}")
	public Response getDeviceInformation(@PathParam("name") String name,
			@PathParam("device") String device) {

		device = device.replace("-", " ");

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			DeviceInformation devInfo = telescopes.getRTDeviceInformation(name,
					device);
			return Response.ok(devInfo).build();

		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/register")
	public Response registerTelescope(@QueryParam("name") String name,
			@QueryParam("owner") String owner, @QueryParam("url") String url,
			@QueryParam("user") String user,
			@QueryParam("password") String password) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			telescopes.registerRT(name, owner, url, user, password);
			return Response.ok().build();

		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}/availability")
	public Response registerTelescope(@PathParam("name") String name,
			@QueryParam("from") String from, @QueryParam("to") String to, RTAvailability availability) {

		/*if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		if (from == null && to == null) {
			try {
				RTAvailability availability = telescopes.getRTAvailability(name);
				return Response.ok(availability).build();
			} catch (RTRepositoryException e) {
				return Response.serverError().entity(e.getMessage()).build();
			}
		}
		
		DateFormat format = new SimpleDateFormat("HH-mm-ss");
		RTAvailability availability = new RTAvailability();
		try {
			availability.setStartingTime(format.parse(from));
			availability.setEndingTime(format.parse(to));
		} catch (ParseException e1) {
			return Response.status(Status.BAD_REQUEST).entity(e1.getMessage())
					.build();
		}

		try {
			telescopes.setRTAvailability(name, availability);
			return Response.ok().build();

		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}*/
		
		return Response.ok().build();
	}
}
