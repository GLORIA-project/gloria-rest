package eu.gloria.gs.services.api.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;

import sun.misc.BASE64Encoder;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.repository.user.UserRepositoryException;
import eu.gloria.gs.services.repository.user.UserRepositoryInterface;

public class AuthFilter implements ContainerRequestFilter {

	private static UserRepositoryInterface userRepository = null;

	static {
		GSClientProvider.setHost("saturno.datsi.fi.upm.es");
		GSClientProvider.setPort("8443");

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
		String path = containerRequest.getPath(true);

		// We do allow wadl to be retrieve
		/*
		 * if (method.equals("GET") && (path.equals("application.wadl") || path
		 * .equals("application.wadl/xsd0.xsd"))) { return containerRequest; }
		 */

		// Get the authentification passed in HTTP headers parameters
		String auth = containerRequest.getHeaderValue("authorization");

		// If the user does not have the right (does not provide any HTTP Basic
		// Auth)
		if (auth == null) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}

		// lap : loginAndPassword
		String[] lap = BasicAuth.decode(auth);

		// If login or password fail
		if (lap == null || lap.length != 2) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}

		String actualPassword = sha1(lap[1]);

		try {
			GSClientProvider.setCredentials("gloria-admin", "gl0r1@-@dm1n");
						
			if (!userRepository.authenticateUser(lap[0], actualPassword)) {
				if (!userRepository.authenticateUser(lap[0], lap[1])) {
					throw new WebApplicationException(Status.UNAUTHORIZED);
				} else {
					actualPassword = lap[1];
				}
			}
		} catch (UserRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sr.setAttribute("user", lap[0]);
		sr.setAttribute("password", actualPassword);

		return containerRequest;
	}

	private String sha1(String input) {
		MessageDigest mDigest = null;
		try {
			mDigest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] bytes;
		try {
			bytes = input.getBytes(("UTF-8"));
			mDigest.update(bytes);
			byte[] digest = mDigest.digest();
			String hash = (new BASE64Encoder()).encode(digest);
			return hash;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return null;
	}
	
	
}
