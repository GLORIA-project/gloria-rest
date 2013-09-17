/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
		GSClientProvider.setHost("localhost");
		GSClientProvider.setPort("8443");
		telescopes = GSClientProvider.getRTRepositoryClient();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list")
	public Response getAllTelescopes() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<String> completeNames = new ArrayList<>();

			List<String> observatories = telescopes.getAllObservatoryNames();
			for (String observatory : observatories) {
				List<String> names = telescopes
						.getAllRTInObservatory(observatory);
				completeNames.addAll(names);
			}

			return Response.ok(completeNames).build();

		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}/devices")
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
				List<DeviceInformation> devices = new ArrayList<DeviceInformation>();

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
	@Path("/{name}")
	public Response getRTInformation(@PathParam("name") String name) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			Map<String, Object> rtInfo = new LinkedHashMap<>();
			
			rtInfo.put("description" , telescopes.getRTDescription(name));
			rtInfo.put("owner" , telescopes.getRTOwner(name));
			rtInfo.put("coordinates" , telescopes.getRTCoordinates(name));
			
			return Response.ok(rtInfo).build();

		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}/devices")
	public Response getDeviceInformation(@PathParam("name") String name, Object devName) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			DeviceInformation devInfo = telescopes.getRTDeviceInformation(name,
					(String)devName);
			return Response.ok(devInfo).build();

		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/register")
	public Response registerTelescope(@QueryParam("name") String name,
			RegisterTelescopeRequest data) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			telescopes.registerRT(name, data.getOwner(), data.getUrl(),
					data.getUser(), data.getPassword());
			return Response.ok().build();

		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}/availability")
	public Response setRTAvailability(@PathParam("name") String name,
			RTAvailability availability) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			telescopes.setRTAvailability(name, availability);
			return Response.ok().build();
		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}/availability")
	public Response getRTAvailability(@PathParam("name") String name) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			RTAvailability availability = telescopes.getRTAvailability(name);

			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			Map<String, String> availabilityFormatted = new LinkedHashMap<>();

			availabilityFormatted.put("startingTime",
					dateFormat.format(availability.getStartingTime()));
			availabilityFormatted.put("endingTime",
					dateFormat.format(availability.getEndingTime()));

			return Response.ok(availabilityFormatted).build();
		} catch (RTRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
}
