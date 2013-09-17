/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

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
import javax.ws.rs.core.Response.Status;
import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.teleoperation.base.DeviceOperationFailedException;
import eu.gloria.gs.services.teleoperation.ccd.CCDTeleoperationException;
import eu.gloria.gs.services.teleoperation.ccd.CCDTeleoperationInterface;
import eu.gloria.gs.services.teleoperation.ccd.ImageExtensionFormat;
import eu.gloria.gs.services.teleoperation.ccd.ImageNotAvailableException;
import eu.gloria.gs.services.teleoperation.dome.DomeTeleoperationException;
import eu.gloria.gs.services.teleoperation.dome.DomeTeleoperationInterface;
import eu.gloria.gs.services.teleoperation.focuser.FocuserTeleoperationException;
import eu.gloria.gs.services.teleoperation.focuser.FocuserTeleoperationInterface;
import eu.gloria.gs.services.teleoperation.fw.FilterWheelTeleoperationException;
import eu.gloria.gs.services.teleoperation.fw.FilterWheelTeleoperationInterface;
import eu.gloria.gs.services.teleoperation.generic.GenericTeleoperationException;
import eu.gloria.gs.services.teleoperation.generic.GenericTeleoperationInterface;
import eu.gloria.gs.services.teleoperation.mount.MountTeleoperationException;
import eu.gloria.gs.services.teleoperation.mount.MountTeleoperationInterface;
import eu.gloria.gs.services.teleoperation.scam.SCamTeleoperationException;
import eu.gloria.gs.services.teleoperation.scam.SCamTeleoperationInterface;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */
@Path("/teleoperation")
public class Teleoperation {

	@Context
	HttpServletRequest request;

	private static MountTeleoperationInterface mounts;
	private static DomeTeleoperationInterface domes;
	private static SCamTeleoperationInterface scams;
	private static CCDTeleoperationInterface ccds;
	private static FilterWheelTeleoperationInterface filters;
	private static FocuserTeleoperationInterface focusers;
	private static GenericTeleoperationInterface generics;

	static {
		GSClientProvider.setHost("localhost");
		GSClientProvider.setPort("8443");
		mounts = GSClientProvider.getMountTeleoperationClient();
		domes = GSClientProvider.getDomeTeleoperationClient();
		scams = GSClientProvider.getSCamTeleoperationClient();
		filters = GSClientProvider.getFilterWheelTeleoperationClient();
		focusers = GSClientProvider.getFocuserTeleoperationClient();
		ccds = GSClientProvider.getCCDTeleoperationClient();
		generics = GSClientProvider.getGenericTeleoperationClient();
	}

	@GET
	@Path("/filter/list/{rt}/{fw}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFilters(@PathParam("rt") String rt,
			@PathParam("fw") String fw) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			List<String> filterList = filters.getFilters(rt, fw);

			return Response.ok(filterList).build();
		} catch (FilterWheelTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/generic/startInteractive/{rt}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response startInteractive(@PathParam("rt") String rt,
			@QueryParam("seconds") Long seconds) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			if (seconds == null) {
				generics.startTeleoperation(rt);
			} else {
				generics.notifyTeleoperation(rt, seconds);
			}

			return Response.ok().build();
		} catch (GenericTeleoperationException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/generic/stopInteractive/{rt}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response startInteractive(@PathParam("rt") String rt) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			generics.stopTeleoperation(rt);

			return Response.ok().build();
		} catch (GenericTeleoperationException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/focus/move/{rt}/{focus}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response focusIn(@PathParam("rt") String rt,
			@PathParam("focus") String focus, @QueryParam("steps") Long steps) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		if (steps == null) {
			steps = (long) 100;
		}

		try {
			focusers.moveRelative(rt, focus, steps);

			return Response.ok().build();
		} catch (FocuserTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/ccd/startContinue/{rt}/{ccd}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response startContinueMode(@PathParam("rt") String rt,
			@PathParam("ccd") String ccd) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		ccd = ccd.replace("-", " ");

		try {
			String id = ccds.startContinueMode(rt, ccd);

			return Response.ok(id).build();

		} catch (CCDTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/ccd/attributes/{rt}/{ccd}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setAttributes(@PathParam("rt") String rt,
			@PathParam("ccd") String ccd,
			@QueryParam("exposure") Double exposure,
			@QueryParam("brightness") Long brightness,
			@QueryParam("gain") Long gain, @QueryParam("gamma") Long gamma) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		ccd = ccd.replace("-", " ");

		if (exposure != null) {

			try {
				ccds.setExposureTime(rt, ccd, exposure);
			} catch (CCDTeleoperationException e) {
				return Response.serverError().entity(e.getMessage()).build();
			} catch (DeviceOperationFailedException e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(e.getMessage()).build();
			}
		}

		if (brightness != null) {

			try {
				ccds.setBrightness(rt, ccd, brightness);
			} catch (CCDTeleoperationException e) {
				return Response.serverError().entity(e.getMessage()).build();
			} catch (DeviceOperationFailedException e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(e.getMessage()).build();
			}
		}

		if (gamma != null) {

			try {
				ccds.setGamma(rt, ccd, gamma);
			} catch (CCDTeleoperationException e) {
				return Response.serverError().entity(e.getMessage()).build();
			} catch (DeviceOperationFailedException e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(e.getMessage()).build();
			}
		}

		if (gain != null) {

			try {
				ccds.setGain(rt, ccd, gain);
			} catch (CCDTeleoperationException e) {
				return Response.serverError().entity(e.getMessage()).build();
			} catch (DeviceOperationFailedException e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(e.getMessage()).build();
			}
		}

		if (exposure == null && brightness == null && gamma == null
				&& gain == null) {
			String response;
			try {
				response = "{\"exposure\":" + ccds.getExposureTime(rt, ccd)
						+ ", \"brightness\":" + ccds.getBrightness(rt, ccd)
						+ ", \"gamma\":" + ccds.getGamma(rt, ccd)
						+ ", \"gain\":" + ccds.getGain(rt, ccd) + "}";
			} catch (CCDTeleoperationException e) {
				return Response.serverError().entity(e.getMessage()).build();
			} catch (DeviceOperationFailedException e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(e.getMessage()).build();
			}

			return Response.ok(response).build();
		} else {
			return Response.ok().build();
		}
	}

	@GET
	@Path("/ccd/startExposure/{rt}/{ccd}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response startExposure(@PathParam("rt") String rt,
			@PathParam("ccd") String ccd,
			@QueryParam("exposure") Double exposure) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		ccd = ccd.replace("-", " ");

		try {
			if (exposure != null) {
				ccds.setExposureTime(rt, ccd, exposure);
			}
			String id = ccds.startExposure(rt, ccd);

			return Response.ok(id).build();

		} catch (CCDTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/ccd/url/{rt}/{ccd}/{lid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCCDImageUrl(@PathParam("rt") String rt,
			@PathParam("ccd") String ccd, @PathParam("lid") String lid,
			@QueryParam("format") String format) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		ccd = ccd.replace("-", " ");

		if (format == null) {
			format = "JPG";
		}

		try {
			String url = ccds.getImageURL(rt, ccd, lid,
					ImageExtensionFormat.valueOf(format));

			return Response.ok(url).build();
		} catch (CCDTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (ImageNotAvailableException e) {
			return Response.status(Status.NOT_FOUND).entity(e.getMessage())
					.build();
		}
	}

	@GET
	@Path("/ccd/stopContinue/{rt}/{ccd}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopContinueMode(@PathParam("rt") String rt,
			@PathParam("ccd") String ccd) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			ccds.stopContinueMode(rt, ccd.replace("-", " "));

			return Response.ok().build();
		} catch (CCDTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/scam/url/{rt}/{scam}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSCamUrl(@PathParam("rt") String rt,
			@PathParam("scam") String scam) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			String url = scams.getImageURL(rt, scam);

			return Response.ok(url).build();
		} catch (SCamTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/mount/slewRADEC/{rt}/{mount}")
	public Response slewToObject(@PathParam("rt") String rt,
			@PathParam("mount") String mount,
			@QueryParam("object") String object) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			mounts.slewToObject(rt, mount, object);

			return Response.ok().build();
		} catch (MountTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/mount/park/{rt}/{mount}")
	public Response parkMount(@PathParam("rt") String rt,
			@PathParam("mount") String mount) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			mounts.park(rt, mount);

			return Response.ok().build();
		} catch (MountTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/mount/slewObject/{rt}/{mount}")
	public Response slewToRADEC(@PathParam("rt") String rt,
			@PathParam("mount") String mount, @QueryParam("ra") double ra,
			@QueryParam("dec") double dec) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			mounts.slewToCoordinates(rt, mount, ra, dec);

			return Response.ok().build();
		} catch (MountTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/mount/move/{rt}/{mount}/{direction}")
	public Response moveDirection(@PathParam("rt") String rt,
			@PathParam("mount") String mount,
			@PathParam("direction") String direction) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			if (direction.toLowerCase().equals("north")) {
				mounts.moveNorth(rt, mount);
			} else if (direction.toLowerCase().equals("south")) {
				mounts.moveSouth(rt, mount);
			} else if (direction.toLowerCase().equals("east")) {
				mounts.moveEast(rt, mount);
			} else if (direction.toLowerCase().equals("west")) {
				mounts.moveWest(rt, mount);
			}

			return Response.ok().build();
		} catch (MountTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/mount/slewRate/{rt}/{mount}")
	public Response manageSlewRate(@PathParam("rt") String rt,
			@PathParam("mount") String mount, @QueryParam("rate") String rate) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			if (rate != null) {
				mounts.setSlewRate(rt, mount, rate);
			} else {
				// String currentRate = mounts.getSlewRate(rt, mount);
			}

			return Response.ok().build();
		} catch (MountTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/dome/open/{rt}/{dome}")
	public Response openDome(@PathParam("rt") String rt,
			@PathParam("dome") String dome) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			domes.open(rt, dome);

			return Response.ok().build();
		} catch (DomeTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/dome/close/{rt}/{dome}")
	public Response closeDome(@PathParam("rt") String rt,
			@PathParam("dome") String dome) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			domes.close(rt, dome);

			return Response.ok().build();
		} catch (DomeTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/dome/park/{rt}/{dome}")
	public Response parkDome(@PathParam("rt") String rt,
			@PathParam("dome") String dome) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			domes.park(rt, dome);

			return Response.ok().build();
		} catch (DomeTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/dome/azimuth/{rt}/{dome}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDomeAzimuth(@PathParam("rt") String rt,
			@PathParam("dome") String dome) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			double azimuth = domes.getAzimuth(rt, dome);

			return Response.ok(azimuth).build();
		} catch (DomeTeleoperationException e) {
			return Response.serverError().entity(e.getMessage()).build();
		} catch (DeviceOperationFailedException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).build();
		}
	}
}
