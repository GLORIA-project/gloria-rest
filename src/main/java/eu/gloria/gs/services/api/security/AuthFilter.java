package eu.gloria.gs.services.api.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.ApplicationContext;

import sun.misc.BASE64Encoder;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import eu.gloria.gs.services.api.data.UserDataAdapter;
import eu.gloria.gs.services.api.data.dbservices.UserDataAdapterException;
import eu.gloria.gs.services.api.data.dbservices.UserEntry;
import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.repository.user.UserRepositoryException;
import eu.gloria.gs.services.repository.user.UserRepositoryInterface;

public class AuthFilter implements ContainerRequestFilter {

	private static UserRepositoryInterface userRepository = null;

	private static String adminUsername;
	private static String adminPassword;
	private static UserDataAdapter userAdapter;

	static {

		ApplicationContext context = ApplicationContextProvider
				.getApplicationContext();

		String hostName = (String) context.getBean("hostName");
		String hostPort = (String) context.getBean("hostPort");

		GSClientProvider.setHost(hostName);
		GSClientProvider.setPort(hostPort);

		adminUsername = (String) context.getBean("adminUsername");
		adminPassword = (String) context.getBean("adminPassword");

		userAdapter = (UserDataAdapter) context.getBean("userDataAdapter");
		userRepository = GSClientProvider.getUserRepositoryClient();
	}

	@Context
	HttpServletRequest sr;

	/**
	 * Apply the filter : check input request, validate or not with user auth
	 * 
	 * @param containerRequest
	 *            The request from Tomcat server
	 */
	@Override
	public ContainerRequest filter(ContainerRequest containerRequest)
			throws WebApplicationException {
		// GET, POST, PUT, DELETE, ...
		String method = containerRequest.getMethod();
		// myresource/get/56bCA for example
		// String path = containerRequest.getPath(true);

		if (method.equals("OPTIONS")) {
			throw new WebApplicationException(Status.OK);
		}

		// We do allow wadl to be retrieve
		/*
		 * if (method.equals("GET") && (path.equals("application.wadl") || path
		 * .equals("application.wadl/xsd0.xsd"))) { return containerRequest; }
		 */

		// Get the authentification passed in HTTP headers parameters
		String auth = containerRequest.getHeaderValue("authorization");

		// If the user does not have the right (does not provide any HTTP Basic
		// Auth)
		/*
		 * if (auth == null) { //throw new
		 * WebApplicationException(Status.UNAUTHORIZED); }
		 */

		GSClientProvider.setCredentials("dummy", "neh");

		if (auth != null) {
			// lap : loginAndPassword
			String[] lap = BasicAuth.decode(auth);

			// If login or password fail
			if (lap == null || lap.length != 2) {
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}

			UserEntry entry = null;
			String name = null;
			String actualPassword = null;

			try {
				entry = userAdapter.getUserInformationByToken(lap[1]);
			} catch (UserDataAdapterException e) {
			}

			boolean authenticated = false;

			if (entry != null) {

				if (new Date().getTime()
						- entry.getTokenCreationDate().getTime() < 1800000) {
					name = entry.getName();
					actualPassword = entry.getPassword();
					try {
						userAdapter.updateLastCreationDate(name);
						authenticated = true;
					} catch (UserDataAdapterException e) {
						//throw new WebApplicationException(Status.UNAUTHORIZED);
					}
				}
			}

			if (!authenticated) {
				actualPassword = SHA1.encode(lap[1]);
				name = lap[0];

				try {
					GSClientProvider.setCredentials(adminUsername,
							adminPassword);

					if (!userRepository
							.authenticateUser(name, actualPassword)) {
						if (!userRepository.authenticateUser(name, lap[1])) {
							throw new WebApplicationException(
									Status.UNAUTHORIZED);
						} else {
							actualPassword = lap[1];
						}
					}					

				} catch (UserRepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			sr.setAttribute("user", name);
			sr.setAttribute("password", actualPassword);
		}

		return containerRequest;
	}
}
