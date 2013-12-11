/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.gloria.gs.services.api.data.UserDataAdapter;
import eu.gloria.gs.services.api.data.dbservices.UserDataAdapterException;
import eu.gloria.gs.services.core.client.GSClientProvider;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */

@Path("/users")
public class Users extends GResource {

	@Context
	HttpServletRequest request;

	private static UserDataAdapter userAdapter = (UserDataAdapter) context.getBean("userDataAdapter");

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/authenticate")
	public Response authenticateUser(@QueryParam("verify") boolean verify) {

		if (verify) {
			return Response.ok(new ArrayList<>()).build();
		}

		String user = null;
		String password = null;

		if (request.getAttribute("user") != null) {

			user = (String) request.getAttribute("user");
			password = (String) request.getAttribute("password");

			GSClientProvider.setCredentials(user, password);
		}

		try {
			String token = userAdapter.createToken(user, password);
			return Response.ok(JSONConverter.toJSON(token)).build();
		} catch (UserDataAdapterException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
}
