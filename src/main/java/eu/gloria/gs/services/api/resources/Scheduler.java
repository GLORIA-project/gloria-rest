/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.spi.resource.Singleton;

import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.scheduler.SchedulerException;
import eu.gloria.gs.services.scheduler.SchedulerInterface;
import eu.gloria.gs.services.scheduler.brain.InvalidObservingPlanException;
import eu.gloria.gs.services.scheduler.brain.MaxUserSchedulesException;
import eu.gloria.gs.services.scheduler.data.ObservingPlanInformation;
import eu.gloria.gs.services.scheduler.data.ScheduleInformation;
import eu.gloria.gs.services.scheduler.data.ScheduleNotFoundException;
import eu.gloria.gs.services.scheduler.data.SchedulerDatabaseException;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */

@Singleton
@Path("/scheduler")
public class Scheduler {

	@Context
	HttpServletRequest request;

	private static SchedulerInterface scheduler;

	static {
		GSClientProvider.setHost("venus.datsi.fi.upm.es");
		GSClientProvider.setPort("8443");

		scheduler = GSClientProvider.getSchedulerClient();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/plans/{id}")
	public Response getOPInformation(@PathParam("id") int id) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			ScheduleInformation schInfo = scheduler.getScheduleInformation(id);

			return Response.ok(schInfo).build();

		} catch (SchedulerException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/plans/request")
	public Response schedule(ObservingPlanInformation opInfo) {

		String username = (String) request.getAttribute("user");

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(username,
					(String) request.getAttribute("password"));
		}

		try {
			opInfo.setUser(username);
			int id = scheduler.schedule(opInfo);
			return Response.ok(id).build();

		} catch (SchedulerException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		} catch (SchedulerDatabaseException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		} catch (MaxUserSchedulesException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (InvalidObservingPlanException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage())
					.build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/plans/active")
	public Response getMyActivePlans() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			List<ScheduleInformation> schInfos = scheduler.getMyActivePlans();

			return Response.ok(schInfos).build();

		} catch (SchedulerException | ScheduleNotFoundException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/plans/inactive")
	public Response getMyInactivePlans() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			List<ScheduleInformation> schInfos = scheduler.getMyInactivePlans();

			return Response.ok(schInfos).build();

		} catch (SchedulerException | ScheduleNotFoundException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{rt}/plans")
	public Response getRTPlans(@PathParam("rt") String rt) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			List<ScheduleInformation> schInfos = scheduler.getAllRTPlans(rt);

			return Response.ok(schInfos).build();

		} catch (SchedulerException | ScheduleNotFoundException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{rt}/plans/active")
	public Response getActiveRTPlans(@PathParam("rt") String rt) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			List<ScheduleInformation> schInfos = scheduler.getActiveRTPlans(rt);

			return Response.ok(schInfos).build();

		} catch (SchedulerException | ScheduleNotFoundException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}
}
