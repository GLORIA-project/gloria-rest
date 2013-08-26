/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import javax.ws.rs.core.Response.Status;
import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.experiment.base.data.ReservationInformation;
import eu.gloria.gs.services.experiment.base.data.TimeSlot;
import eu.gloria.gs.services.experiment.base.reservation.ExperimentReservationArgumentException;
import eu.gloria.gs.services.experiment.base.reservation.MaxReservationTimeException;
import eu.gloria.gs.services.experiment.base.reservation.NoReservationsAvailableException;
import eu.gloria.gs.services.experiment.online.OnlineExperimentException;
import eu.gloria.gs.services.experiment.online.OnlineExperimentInterface;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */
@Path("/experiments")
public class Experiments extends CORSResource {

	@Context
	HttpServletRequest request;

	private static OnlineExperimentInterface experiments;

	static {
		GSClientProvider.setHost("saturno.datsi.fi.upm.es");
		GSClientProvider.setPort("8443");
		experiments = GSClientProvider.getOnlineExperimentClient();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}")
	public String getMessage(@PathParam("name") String name) {
		return "{'name':" + name + "}";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/list")
	public Response listExperiments() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<String> names = experiments.getAllOnlineExperiments();

			return this.makeCORS(Response.ok(names));

		} catch (OnlineExperimentException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/active")
	public Response listActiveExperiments() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<ReservationInformation> reservations = experiments
					.getMyCurrentReservations();

			return this.makeCORS(Response.ok(reservations));

		} catch (OnlineExperimentException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		} catch (NoReservationsAvailableException e) {
			return this.makeCORS(Response.ok(new ArrayList<String>()));
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/pending")
	public Response listPendingExperiments() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<ReservationInformation> reservations = experiments
					.getMyPendingReservations();

			return this.makeCORS(Response.ok(reservations));

		} catch (OnlineExperimentException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		} catch (NoReservationsAvailableException e) {
			return this.makeCORS(Response.ok(new ArrayList<String>()));
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/slots/available/{year}/{month}/{day}")
	public Response listAvailableTimeSlots(@PathParam("year") String year,
			@PathParam("month") String month, @PathParam("day") String day,
			ListAvailableTimeSlotsRequest data) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<TimeSlot> timeSlots = experiments.getAvailableReservations(
					data.getExperiment(), data.getTelescopes());

			Calendar calendar = Calendar.getInstance();
			List<TimeSlot> filteredTimeSlots = new ArrayList<>();

			for (TimeSlot timeSlot : timeSlots) {
				calendar.setTime(timeSlot.getBegin());
				if (calendar.get(Calendar.DAY_OF_MONTH) == Integer.valueOf(day)
						&& calendar.get(Calendar.MONTH) == Integer
								.valueOf(month)
						&& calendar.get(Calendar.YEAR) == Integer.valueOf(year)) {

					filteredTimeSlots.add(timeSlot);

				}
			}

			return this.makeCORS(Response.ok(filteredTimeSlots));
		} catch (OnlineExperimentException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		} catch (ExperimentReservationArgumentException e) {
			return this.makeCORS(Response.status(Status.BAD_REQUEST).entity(
					e.getMessage()));
		}
	}

	@GET
	@Path("/online/reserve/{experiment}")
	public Response reserveExperiment(
			@PathParam("experiment") String experiment,
			@QueryParam("rts") List<String> rts,
			@QueryParam("from") String from, @QueryParam("to") String to) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			TimeSlot ts = new TimeSlot();

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

			try {
				ts.setBegin(format.parse(from));
				ts.setEnd(format.parse(to));
			} catch (ParseException e) {
				return this.makeCORS(Response.status(Status.BAD_REQUEST)
						.entity(e.getMessage()));
			}

			experiments.reserveExperiment(experiment, rts, ts);
			return this.makeCORS(Response.ok());

		} catch (OnlineExperimentException e) {
			return this.makeCORS(Response.serverError().entity(e.getMessage()));
		} catch (NoReservationsAvailableException e) {
			return this.makeCORS(Response.status(Status.NOT_ACCEPTABLE).entity(
					e.getMessage()));
		} catch (ExperimentReservationArgumentException e) {
			return this.makeCORS(Response.status(Status.NOT_ACCEPTABLE).entity(
					e.getMessage()));
		} catch (MaxReservationTimeException e) {
			return this.makeCORS(Response.status(Status.NOT_ACCEPTABLE).entity(
					e.getMessage()));
		}
	}
}
