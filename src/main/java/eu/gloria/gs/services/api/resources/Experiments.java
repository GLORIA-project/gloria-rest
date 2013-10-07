/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.experiment.ExperimentException;
import eu.gloria.gs.services.experiment.ExperimentInterface;
import eu.gloria.gs.services.experiment.base.data.ExperimentInformation;
import eu.gloria.gs.services.experiment.base.data.FeatureInformation;
import eu.gloria.gs.services.experiment.base.data.NoSuchExperimentException;
import eu.gloria.gs.services.experiment.base.data.OperationInformation;
import eu.gloria.gs.services.experiment.base.data.ParameterInformation;
import eu.gloria.gs.services.experiment.base.data.ReservationInformation;
import eu.gloria.gs.services.experiment.base.data.TimeSlot;
import eu.gloria.gs.services.experiment.base.models.DuplicateExperimentException;
import eu.gloria.gs.services.experiment.base.models.ExperimentFeature;
import eu.gloria.gs.services.experiment.base.operations.ExperimentOperation;
import eu.gloria.gs.services.experiment.base.operations.ExperimentOperationException;
import eu.gloria.gs.services.experiment.base.operations.NoSuchOperationException;
import eu.gloria.gs.services.experiment.base.parameters.ExperimentParameter;
import eu.gloria.gs.services.experiment.base.parameters.ExperimentParameterException;
import eu.gloria.gs.services.experiment.base.parameters.ObjectResponse;
import eu.gloria.gs.services.experiment.base.parameters.ParameterType;
import eu.gloria.gs.services.experiment.base.reservation.ExperimentNotInstantiatedException;
import eu.gloria.gs.services.experiment.base.reservation.ExperimentReservationArgumentException;
import eu.gloria.gs.services.experiment.base.reservation.MaxReservationTimeException;
import eu.gloria.gs.services.experiment.base.reservation.NoReservationsAvailableException;
import eu.gloria.gs.services.experiment.base.reservation.NoSuchReservationException;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */
@Path("/experiments")
public class Experiments {

	@Context
	HttpServletRequest request;
	@Context
	private HttpHeaders headers;

	private static ExperimentInterface experiments;

	static {
		GSClientProvider.setHost("localhost");
		GSClientProvider.setPort("8443");
		experiments = GSClientProvider.getOnlineExperimentClient();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/register")
	public Response registerOnlineExperiment(@QueryParam("name") String name) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			experiments.createOnlineExperiment(name);

			return Response.ok().build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DuplicateExperimentException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/offline/register")
	public Response registerOfflineExperiment(@QueryParam("name") String name) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			experiments.createOfflineExperiment(name);

			return Response.ok().build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DuplicateExperimentException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/list")
	public Response listOnlineExperiments() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<String> names = experiments.getAllOnlineExperiments();

			return Response.ok(names).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/offline/list")
	public Response listOfflineExperiments() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<String> names = experiments.getAllOfflineExperiments();

			return Response.ok(names).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/active")
	public Response listActiveExperiments() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<ReservationInformation> reservations = experiments
					.getMyCurrentReservations();

			return Response.ok(reservations).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoReservationsAvailableException e) {
			return Response.ok(new ArrayList<String>()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/pending")
	public Response listPendingExperiments() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<ReservationInformation> reservations = experiments
					.getMyPendingReservations();

			return Response.ok(reservations).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoReservationsAvailableException e) {
			return Response.ok(new ArrayList<String>()).build();
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
			List<TimeSlot> filteredTimeSlots = new ArrayList<TimeSlot>();

			for (TimeSlot timeSlot : timeSlots) {
				calendar.setTime(timeSlot.getBegin());
				if (calendar.get(Calendar.DAY_OF_MONTH) == Integer.valueOf(day)
						&& calendar.get(Calendar.MONTH) == Integer
								.valueOf(month)
						&& calendar.get(Calendar.YEAR) == Integer.valueOf(year)) {

					filteredTimeSlots.add(timeSlot);

				}
			}

			return Response.ok(filteredTimeSlots).build();
		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (ExperimentReservationArgumentException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage())
					.build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/reserve")
	public Response reserveExperiment(ReserveOnlineExperimentRequest data) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			TimeSlot timeSlot = new TimeSlot();
			timeSlot.setBegin(data.getBegin());
			timeSlot.setEnd(data.getEnd());

			experiments.reserveExperiment(data.getExperiment(),
					data.getTelescopes(), timeSlot);
			return Response.ok().build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoReservationsAvailableException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (ExperimentReservationArgumentException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (MaxReservationTimeException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/offline/apply")
	public Response applyForExperiment(
			@QueryParam("experiment") String experiment) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			experiments.applyForExperiment(experiment);
			return Response.ok().build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoReservationsAvailableException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/parameters/{parameter}")
	public Response getParameterContextValue(@PathParam("rid") int rid,
			@PathParam("parameter") String parameter,
			@QueryParam("tree") String tree) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			ReservationInformation resInfo = experiments
					.getReservationInformation(rid);

			ExperimentInformation expInfo = experiments
					.getExperimentInformation(resInfo.getExperiment());

			List<ParameterInformation> parameterInfos = expInfo.getParameters();

			String parameterTree = parameter;
			if (tree != null) {
				parameterTree = parameterTree + "." + tree;
			}

			ObjectResponse response = experiments.getExperimentParameterValue(
					rid, parameterTree);

			Object value = null;
			Class<?> valueType = Object.class;
			Class<?> elementType = null;

			for (ParameterInformation paramInfo : parameterInfos) {
				if (paramInfo.getName().equals(parameter)) {
					ParameterType type = paramInfo.getParameter().getType();
					valueType = type.getValueType();
					elementType = type.getElementType();
				}
			}

			value = JSONConverter.fromJSON((String) response.content,
					valueType, elementType);

			return Response.ok(value).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchReservationException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (ExperimentNotInstantiatedException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/parameters/{parameter}")
	public Response setParameterContextValue(@PathParam("rid") int rid,
			@PathParam("parameter") String parameter,
			@QueryParam("tree") String tree, Object data) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		Object castedValue = data;

		try {

			ReservationInformation resInfo = experiments
					.getReservationInformation(rid);

			ExperimentInformation expInfo = experiments
					.getExperimentInformation(resInfo.getExperiment());

			List<ParameterInformation> parameterInfos = expInfo.getParameters();

			for (ParameterInformation paramInfo : parameterInfos) {
				if (paramInfo.getName().equals(parameter)) {
					castedValue = JSONConverter.toJSON(data);
				}
			}
		} catch (ExperimentException | NoSuchExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}

		try {

			String parameterTree = parameter;
			if (tree != null) {
				parameterTree = parameterTree + "." + tree;
			}

			experiments.setExperimentParameterValue(rid, parameterTree,
					new ObjectResponse(castedValue));
			return Response.ok().build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchReservationException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (ExperimentNotInstantiatedException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/operations")
	public Response listOperations(@PathParam("experiment") String experiment,
			@QueryParam("detailed") boolean detailed) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			List<OperationInformation> operations = experiments
					.getExperimentInformation(experiment).getOperations();

			if (!detailed) {
				List<String> opNames = new ArrayList<>();

				if (operations != null) {
					for (OperationInformation opInfo : operations) {
						opNames.add(opInfo.getName());
					}
				}

				return Response.ok(opNames).build();
			} else {
				return Response.ok(operations).build();
			}

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage())
					.build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/parameters")
	public Response listParameters(@PathParam("experiment") String experiment,
			@QueryParam("detailed") boolean detailed) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			List<ParameterInformation> parameters = experiments
					.getExperimentInformation(experiment).getParameters();

			if (!detailed) {
				List<String> paramNames = new ArrayList<>();

				if (parameters != null) {
					for (ParameterInformation paramInfo : parameters) {
						paramNames.add(paramInfo.getName());
					}
				}

				return Response.ok(paramNames).build();
			} else {
				return Response.ok(parameters).build();
			}

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage())
					.build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/parameters/{parameter}")
	public Response showParameterInformation(
			@PathParam("experiment") String experiment,
			@PathParam("parameter") String parameter) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			ParameterInformation paramInfo = experiments
					.getExperimentInformation(experiment).getParameter(
							parameter);

			return Response.ok(paramInfo).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage())
					.build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/operations/{operation}")
	public Response showOperationInformation(
			@PathParam("experiment") String experiment,
			@PathParam("operation") String operation) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			OperationInformation opInfo = experiments.getExperimentInformation(
					experiment).getOperation(operation);

			return Response.ok(opInfo).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage())
					.build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/execute/{operation}")
	public Response executeOperation(@PathParam("rid") int rid,
			@PathParam("operation") String operation) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			experiments.executeExperimentOperation(rid, operation);

			return Response.ok().build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchReservationException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (ExperimentNotInstantiatedException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (ExperimentOperationException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (NoSuchOperationException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		} catch (ExperimentParameterException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/parameters/add")
	public Response addExperimentParameter(
			@PathParam("experiment") String experiment,
			ParameterInformation paramInfo) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		String[] argStr = new String[paramInfo.getArguments().length];

		try {
			int i = 0;
			for (Object arg : paramInfo.getArguments()) {
				argStr[i] = JSONConverter.toJSON(arg);

				i++;
			}

			paramInfo.setArguments(argStr);

			experiments.addExperimentParameter(experiment, paramInfo);
			return Response.ok().build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/operations/add")
	public Response addExperimentOperation(
			@PathParam("experiment") String experiment,
			OperationInformation opInfo) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			experiments.addExperimentOperation(experiment, opInfo);
			return Response.ok().build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/features/add")
	public Response addExperimentFeature(
			@PathParam("experiment") String experiment,
			FeatureInformation featureInfo) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			experiments.addExperimentFeature(experiment, featureInfo);
			return Response.ok().build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (NoSuchExperimentException e) {
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/parameters")
	public Response getAllParameters() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			Set<String> parameters = experiments.getAllExperimentParameters();
			return Response.ok(parameters).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/operations")
	public Response getAllOperations() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			Set<String> operations = experiments.getAllExperimentOperations();
			return Response.ok(operations).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/features")
	public Response getAllFeatures() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			Set<String> features = experiments.getAllExperimentFeatures();
			return Response.ok(features).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/parameters/{name}")
	public Response getExperimentParameter(@PathParam("name") String name) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			ExperimentParameter parameter = experiments.getExperimentParameter(name);
			return Response.ok(parameter).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/operations/{name}")
	public Response getExperimentOperation(@PathParam("name") String name) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			ExperimentOperation operation = experiments.getExperimentOperation(name);
			return Response.ok(operation).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/features/{name}")
	public Response getExperimentFeature(@PathParam("name") String name) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			ExperimentFeature feature = experiments.getExperimentFeature(name);
			return Response.ok(feature).build();

		} catch (ExperimentException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
}
