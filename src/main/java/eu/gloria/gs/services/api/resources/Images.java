/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.sun.jersey.spi.resource.Singleton;

import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.repository.image.ImageRepositoryException;
import eu.gloria.gs.services.repository.image.ImageRepositoryInterface;

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
		GSClientProvider.setHost("venus.datsi.fi.upm.es");
		GSClientProvider.setPort("8443");
		
		images = GSClientProvider.getImageRepositoryClient();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list")
	public String listImages() {

		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.set(Calendar.DAY_OF_YEAR,
					calendar.get(Calendar.DAY_OF_YEAR) - 1);

			List<Integer> ids = images.getAllImageIdentifiersByDate(
					calendar.getTime(), new Date());

			return new Gson().toJson(ids);

		} catch (ImageRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
