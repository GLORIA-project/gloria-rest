/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.spi.container.ContainerRequest;

import eu.gloria.gs.services.api.data.UserDataAdapter;
import eu.gloria.gs.services.api.data.dbservices.UserDataAdapterException;
import eu.gloria.gs.services.api.data.dbservices.UserEntry;
import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.repository.user.UserRepositoryException;
import eu.gloria.gs.services.repository.user.UserRepositoryInterface;
import eu.gloria.gs.services.repository.user.data.UserInformation;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */

@Path("/users")
public class Users extends GResource {

	@Context
	HttpServletRequest request;

	private static UserDataAdapter userAdapter = (UserDataAdapter) context
			.getBean("userDataAdapter");
	private static UserRepositoryInterface users = GSClientProvider
			.getUserRepositoryClient();

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

		String userAgent = request.getHeader(ContainerRequest.USER_AGENT);
		String remote = request.getHeader(ContainerRequest.HOST);
		String acceptLanguage = request
				.getHeader(ContainerRequest.ACCEPT_LANGUAGE);

		if (acceptLanguage != null) {
			String[] languages = acceptLanguage.split(";");

			if (languages.length > 0) {
				acceptLanguage = languages[0];
			}
		}

		try {

			List<UserEntry> activeSessions = userAdapter
					.getUserInformation(user);

			if (activeSessions != null) {
				for (UserEntry entry : activeSessions) {
					String regRemote = entry.getRemote();
					String regAgent = entry.getAgent();
					if (remote.equals(regRemote) && userAgent.equals(regAgent)) {
						return Response.ok(
								JSONConverter.toJSON(entry.getToken())).build();
					} else {
						userAdapter.deactivateToken(entry.getToken());
					}
				}
			}

			String token = userAdapter.createToken(user, password,
					acceptLanguage, userAgent, remote);
			return Response.ok(JSONConverter.toJSON(token)).build();
		} catch (UserDataAdapterException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/info")
	public Response getUserInformation() {

		String user = null;
		String password = null;

		if (request.getAttribute("user") != null) {

			user = (String) request.getAttribute("user");
			password = (String) request.getAttribute("password");

			GSClientProvider.setCredentials(user, password);
		}

		try {
			UserInformation userInfo = users.getUserInformation(user);
			return Response.ok(JSONConverter.toJSON(userInfo)).build();
		} catch (UserRepositoryException e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
}
