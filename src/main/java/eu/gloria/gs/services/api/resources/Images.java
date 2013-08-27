/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.sun.jersey.spi.resource.Singleton;

import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.repository.image.ImageRepositoryException;
import eu.gloria.gs.services.repository.image.ImageRepositoryInterface;
import eu.gloria.gs.services.repository.image.data.ImageInformation;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */

@Singleton
@Path("/images")
public class Images {

	@Context
	HttpServletRequest request;

	private static ImageRepositoryInterface images;

	static {
		GSClientProvider.setHost("localhost");
		GSClientProvider.setPort("8443");

		images = GSClientProvider.getImageRepositoryClient();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list")
	public Response listImages() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.set(Calendar.DAY_OF_YEAR,
					calendar.get(Calendar.DAY_OF_YEAR) - 10);

			List<Integer> ids = images.getAllImageIdentifiersByDate(
					calendar.getTime(), new Date());

			return Response.ok(ids).build();

		} catch (ImageRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.ok(new ArrayList<Integer>()).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list/{year}/{month}/{day}")
	public Response listDateImages(@PathParam("year") String year,
			@PathParam("month") String month, @PathParam("day") String day,
			@QueryParam("complete") boolean complete,
			@QueryParam("maxResults") Integer maxResults) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
			calendar.set(Calendar.MONTH, Integer.valueOf(month));
			calendar.set(Calendar.YEAR, Integer.valueOf(year));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);

			Date fromDate = calendar.getTime();

			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);

			Date toDate = calendar.getTime();

			List<Integer> ids = images.getAllImageIdentifiersByDate(fromDate,
					toDate);

			if (ids == null) {
				ids = new ArrayList<>();
			}

			if (maxResults != null) {
				ids = ids.subList(0, Math.min(ids.size(), maxResults));
			}

			if (!complete) {
				return Response.ok(ids).build();
			} else {
				ArrayList<ImageInformation> imageInfos = new ArrayList<>();

				for (Integer id : ids) {
					ImageInformation imageInfo = images
							.getImageInformation(Integer.valueOf(id));
					imageInfos.add(imageInfo);
				}

				return Response.ok(imageInfos).build();
			}

		} catch (ImageRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.ok(new ArrayList<Integer>()).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{imageId}")
	public Response getImageInformation(@PathParam("imageId") String id) {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {

			ImageInformation imageInfo = images.getImageInformation(Integer
					.valueOf(id));

			return Response.ok(imageInfo).build();

		} catch (ImageRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(Status.BAD_REQUEST).build();
	}

}
