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
import eu.gloria.gs.services.experiment.base.data.ExperimentRuntimeInformation;
import eu.gloria.gs.services.experiment.base.data.NoSuchExperimentException;
import eu.gloria.gs.services.experiment.base.data.OperationInformation;
import eu.gloria.gs.services.experiment.base.data.ParameterInformation;
import eu.gloria.gs.services.experiment.base.data.ReservationInformation;
import eu.gloria.gs.services.experiment.base.data.ResultInformation;
import eu.gloria.gs.services.experiment.base.data.TimeSlot;
import eu.gloria.gs.services.experiment.base.models.DuplicateExperimentException;
import eu.gloria.gs.services.experiment.base.models.ExperimentFeature;
import eu.gloria.gs.services.experiment.base.models.InvalidUserContextException;
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
public class Experiments extends GResource {

	@Context
	HttpServletRequest request;
	@Context
	private HttpHeaders headers;

	private static ExperimentInterface experiments = GSClientProvider
			.getOnlineExperimentClient();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/register")
	public Response registerOnlineExperiment(@QueryParam("name") String name) {

		this.setupRegularAuthorization(request);

		try {
			experiments.createOnlineExperiment(name);

			return this.processSuccess();

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (DuplicateExperimentException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/offline/register")
	public Response registerOfflineExperiment(@QueryParam("name") String name) {

		this.setupRegularAuthorization(request);

		try {
			experiments.createOfflineExperiment(name);

			return this.processSuccess();

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (DuplicateExperimentException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/list")
	public Response listOnlineExperiments() {

		this.setupRegularAuthorization(request);

		try {
			List<String> names = experiments.getAllOnlineExperiments();

			return this.processSuccess(names);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/offline/list")
	public Response listOfflineExperiments() {

		this.setupRegularAuthorization(request);

		try {
			List<String> names = experiments.getAllOfflineExperiments();

			return this.processSuccess(names);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/active")
	public Response listActiveExperiments() {

		this.setupRegularAuthorization(request);

		try {
			List<ReservationInformation> reservations = experiments
					.getMyCurrentReservations();

			return this.processSuccess(reservations);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoReservationsAvailableException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/active")
	public Response listActiveOnlineExperiments() {

		this.setupRegularAuthorization(request);

		try {
			List<ReservationInformation> reservations = experiments
					.getMyCurrentOnlineReservations();

			return this.processSuccess(reservations);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoReservationsAvailableException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/offline/active")
	public Response listActiveOfflineExperiments() {

		this.setupRegularAuthorization(request);

		try {
			List<ReservationInformation> reservations = experiments
					.getMyCurrentOfflineReservations();

			return this.processSuccess(reservations);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoReservationsAvailableException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/pending")
	public Response listPendingExperiments() {

		this.setupRegularAuthorization(request);

		try {
			List<ReservationInformation> reservations = experiments
					.getMyPendingReservations();

			return this.processSuccess(reservations);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoReservationsAvailableException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/pending")
	public Response listPendingOnlineExperiments() {

		this.setupRegularAuthorization(request);

		try {
			List<ReservationInformation> reservations = experiments
					.getMyPendingOnlineReservations();

			return this.processSuccess(reservations);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoReservationsAvailableException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/offline/pending")
	public Response listPendingOfflineExperiments() {

		this.setupRegularAuthorization(request);

		try {
			List<ReservationInformation> reservations = experiments
					.getMyPendingOfflineReservations();

			return this.processSuccess(reservations);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoReservationsAvailableException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/slots/available/{year}/{month}/{day}")
	public Response listAvailableTimeSlots(@PathParam("year") String year,
			@PathParam("month") String month, @PathParam("day") String day,
			ListAvailableTimeSlotsRequest data) {

		this.setupRegularAuthorization(request);

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

			return this.processSuccess(filteredTimeSlots);
		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (ExperimentReservationArgumentException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online/reserve")
	public Response reserveExperiment(ReserveOnlineExperimentRequest data) {

		this.setupRegularAuthorization(request);

		try {
			TimeSlot timeSlot = new TimeSlot();
			timeSlot.setBegin(data.getBegin());
			timeSlot.setEnd(data.getEnd());

			experiments.reserveExperiment(data.getExperiment(),
					data.getTelescopes(), timeSlot);

			return this.processSuccess();

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoReservationsAvailableException
				| ExperimentReservationArgumentException
				| MaxReservationTimeException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/offline/apply")
	public Response applyForExperiment(
			@QueryParam("experiment") String experiment) {

		this.setupRegularAuthorization(request);

		try {
			experiments.applyForExperiment(experiment);

			return this.processSuccess();

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoReservationsAvailableException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		} catch (NoSuchExperimentException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/remaining")
	public Response getContextRemainingTime(@PathParam("rid") int rid) {

		this.setupRegularAuthorization(request);

		try {

			ExperimentRuntimeInformation runtimeInfo = experiments
					.getExperimentRuntimeInformation(rid);
			long remaining = runtimeInfo.getRemainingTime();

			return this.processSuccess(remaining);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchReservationException e) {
			return this.processError(Status.NOT_FOUND, e);
		} catch (ExperimentNotInstantiatedException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/elapsed")
	public Response getContextElapsedTime(@PathParam("rid") int rid) {

		this.setupRegularAuthorization(request);

		try {

			ExperimentRuntimeInformation runtimeInfo = experiments
					.getExperimentRuntimeInformation(rid);
			long elapsed = runtimeInfo.getElapsedTime();

			return this.processSuccess(elapsed);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchReservationException e) {
			return this.processError(Status.NOT_FOUND, e);
		} catch (ExperimentNotInstantiatedException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}")
	public Response getExperimentContext(@PathParam("rid") int rid) {

		this.setupRegularAuthorization(request);

		try {

			ObjectResponse response = experiments.getExperimentContext(rid);

			return this.processSuccess(response.content);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchReservationException e) {
			return this.processError(Status.NOT_FOUND, e);
		} catch (ExperimentNotInstantiatedException | InvalidUserContextException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/ready")
	public Response isExperimentReady(@PathParam("rid") int rid) {

		this.setupRegularAuthorization(request);

		try {
			boolean instantiated = experiments.isExperimentContextReady(rid);

			return this.processSuccess(instantiated);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchReservationException e) {
			return this.processError(Status.NOT_FOUND, e);
		} catch (ExperimentNotInstantiatedException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/parameters/{parameter}")
	public Response getParameterContextValue(@PathParam("rid") int rid,
			@PathParam("parameter") String parameter,
			@QueryParam("tree") String tree) {

		this.setupRegularAuthorization(request);

		try {

			ReservationInformation resInfo = experiments
					.getReservationInformation(rid);

			ParameterInformation paramInfo = experiments
					.getParameterInformation(resInfo.getExperiment(), parameter);

			String parameterTree = parameter;
			if (tree != null) {
				tree = tree.replace("%5B", "[");
				tree = tree.replace("%5D", "]");
				parameterTree = parameterTree + "." + tree;
			}

			ObjectResponse response = experiments.getExperimentParameterValue(
					rid, parameterTree);

			Object value = null;
			Class<?> valueType = Object.class;
			Class<?> elementType = null;

			ParameterType type = paramInfo.getParameter().getType();
			valueType = type.getValueType();
			elementType = type.getElementType();

			value = JSONConverter.fromJSON((String) response.content,
					valueType, elementType);

			// value = JSONConverter.toJSON(value); // MAY BE I WILL NEED TO
			// UNCOMMENT THIS LINE!!!!

			return this.processSuccess(value);

		} catch (ExperimentParameterException | ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchExperimentException | NoSuchReservationException e) {
			return this.processError(Status.NOT_FOUND, e);
		} catch (ExperimentNotInstantiatedException | InvalidUserContextException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/parameters/{parameter}")
	public Response setParameterContextValue(@PathParam("rid") int rid,
			@PathParam("parameter") String parameter,
			@QueryParam("tree") String tree, Object data) {

		this.setupRegularAuthorization(request);

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
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (InvalidUserContextException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		}

		try {

			String parameterTree = parameter;
			if (tree != null) {
				tree = tree.replace("%5B", "[");
				tree = tree.replace("%5D", "]");
				parameterTree = parameterTree + "." + tree;
			}

			experiments.setExperimentParameterValue(rid, parameterTree,
					new ObjectResponse(castedValue));
			return this.processSuccess();

		} catch (NoSuchReservationException e) {
			return this.processError(Status.NOT_FOUND, e);
		} catch (ExperimentNotInstantiatedException | InvalidUserContextException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		} catch (ExperimentParameterException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/operations")
	public Response listOperations(@PathParam("experiment") String experiment,
			@QueryParam("detailed") boolean detailed) {

		this.setupRegularAuthorization(request);

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
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchExperimentException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/parameters")
	public Response listParameters(@PathParam("experiment") String experiment,
			@QueryParam("detailed") boolean detailed) {

		this.setupRegularAuthorization(request);

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

				return this.processSuccess(paramNames);
			} else {
				return this.processSuccess(parameters);
			}

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchExperimentException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/parameters/{parameter}")
	public Response showParameterInformation(
			@PathParam("experiment") String experiment,
			@PathParam("parameter") String parameter) {

		this.setupRegularAuthorization(request);

		try {

			ParameterInformation paramInfo = experiments
					.getExperimentInformation(experiment).getParameter(
							parameter);

			return this.processSuccess(paramInfo);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchExperimentException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/operations/{operation}")
	public Response showOperationInformation(
			@PathParam("experiment") String experiment,
			@PathParam("operation") String operation) {

		this.setupRegularAuthorization(request);

		try {

			OperationInformation opInfo = experiments.getExperimentInformation(
					experiment).getOperation(operation);

			return this.processSuccess(opInfo);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchExperimentException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/execute/{operation}")
	public Response executeOperation(@PathParam("rid") int rid,
			@PathParam("operation") String operation) {

		this.setupRegularAuthorization(request);

		try {

			experiments.executeExperimentOperation(rid, operation);

			return this.processSuccess();

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchOperationException | NoSuchExperimentException
				| NoSuchReservationException e) {
			return this.processError(Status.NOT_FOUND, e);
		} catch (ExperimentNotInstantiatedException | InvalidUserContextException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		} catch (ExperimentOperationException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/parameters/add")
	public Response addExperimentParameter(
			@PathParam("experiment") String experiment,
			ParameterInformation paramInfo) {

		this.setupRegularAuthorization(request);

		String[] argStr = new String[paramInfo.getArguments().length];

		try {
			int i = 0;
			for (Object arg : paramInfo.getArguments()) {
				argStr[i] = JSONConverter.toJSON(arg);

				i++;
			}

			paramInfo.setArguments(argStr);

			experiments.addExperimentParameter(experiment, paramInfo);

			return this.processSuccess();

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchExperimentException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/operations/add")
	public Response addExperimentOperation(
			@PathParam("experiment") String experiment,
			OperationInformation opInfo) {

		this.setupRegularAuthorization(request);

		try {
			experiments.addExperimentOperation(experiment, opInfo);
			return this.processSuccess();

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchExperimentException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/features/add")
	public Response addExperimentFeature(
			@PathParam("experiment") String experiment,
			FeatureInformation featureInfo) {

		this.setupRegularAuthorization(request);

		try {
			experiments.addExperimentFeature(experiment, featureInfo);
			return this.processSuccess();

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (NoSuchExperimentException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/parameters")
	public Response getAllParameters() {

		this.setupRegularAuthorization(request);

		try {
			Set<String> parameters = experiments.getAllExperimentParameters();
			return this.processSuccess(parameters);
		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/operations")
	public Response getAllOperations() {

		this.setupRegularAuthorization(request);

		try {
			Set<String> operations = experiments.getAllExperimentOperations();
			return this.processSuccess(operations);
		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/features")
	public Response getAllFeatures() {

		this.setupRegularAuthorization(request);

		try {
			Set<String> features = experiments.getAllExperimentFeatures();
			return this.processSuccess(features);
		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/parameters/{name}")
	public Response getExperimentParameter(@PathParam("name") String name) {

		this.setupRegularAuthorization(request);

		try {
			ExperimentParameter parameter = experiments
					.getExperimentParameter(name);
			return this.processSuccess(parameter);
		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/operations/{name}")
	public Response getExperimentOperation(@PathParam("name") String name) {

		this.setupRegularAuthorization(request);

		try {
			ExperimentOperation operation = experiments
					.getExperimentOperation(name);
			return this.processSuccess(operation);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/engine/features/{name}")
	public Response getExperimentFeature(@PathParam("name") String name) {

		this.setupRegularAuthorization(request);

		try {
			ExperimentFeature feature = experiments.getExperimentFeature(name);
			return this.processSuccess(feature);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{experiment}/results")
	public Response getExperimentResults(
			@PathParam("experiment") String experiment,
			@QueryParam("valuesOnly") boolean valuesOnly) {

		this.setupRegularAuthorization(request);

		try {

			List<ResultInformation> results = experiments
					.getExperimentResults(experiment);

			if (results == null) {
				results = new ArrayList<>();
			}

			if (valuesOnly) {

				List<Object> values = new ArrayList<>();

				for (ResultInformation result : results) {
					values.add(JSONConverter.fromJSON(
							(String) result.getValue(), Object.class, null));
				}

				return this.processSuccess(values);
			}

			for (ResultInformation result : results) {
				result.setValue(JSONConverter.fromJSON(
						(String) result.getValue(), Object.class, null));
			}

			return this.processSuccess(results);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/results")
	public Response getContextResults(@PathParam("rid") int rid,
			@QueryParam("valuesOnly") boolean valuesOnly) {

		this.setupRegularAuthorization(request);

		try {

			List<ResultInformation> results = experiments
					.getContextResults(rid);

			if (results == null) {
				results = new ArrayList<>();
			}

			if (valuesOnly) {

				List<Object> values = new ArrayList<>();

				for (ResultInformation result : results) {
					values.add(JSONConverter.fromJSON(
							(String) result.getValue(), Object.class, null));
				}

				return this.processSuccess(values);
			}

			for (ResultInformation result : results) {
				result.setValue(JSONConverter.fromJSON(
						(String) result.getValue(), Object.class, null));
			}

			return this.processSuccess(results);

		} catch (ExperimentException e) {
			return this.processError(Status.INTERNAL_SERVER_ERROR, e);
		} catch (ExperimentNotInstantiatedException e) {
			return this.processError(Status.NOT_ACCEPTABLE, e);
		} catch (NoSuchReservationException e) {
			return this.processError(Status.NOT_FOUND, e);
		}
	}
}
