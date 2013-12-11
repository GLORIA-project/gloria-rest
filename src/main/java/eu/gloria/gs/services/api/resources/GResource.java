/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.ApplicationContext;

import eu.gloria.gs.services.api.security.ApplicationContextProvider;
import eu.gloria.gs.services.core.client.GSClientProvider;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */
public abstract class GResource {

	private static String adminUsername;
	private static String adminPassword;
	protected static ApplicationContext context = ApplicationContextProvider
			.getApplicationContext();

	static {
		context = ApplicationContextProvider.getApplicationContext();

		String hostName = (String) context.getBean("hostName");
		String hostPort = (String) context.getBean("hostPort");

		adminPassword = (String) context.getBean("adminPassword");
		adminUsername = (String) context.getBean("adminUsername");

		GSClientProvider.setHost(hostName);
		GSClientProvider.setPort(hostPort);
	}

	public static String getAdminUsername() {
		return adminUsername;
	}

	public static void setAdminUsername(String adminUsername) {
		GResource.adminUsername = adminUsername;
	}

	public static String getAdminPassword() {
		return adminPassword;
	}

	public static void setAdminPassword(String adminPassword) {
		GResource.adminPassword = adminPassword;
	}

	protected void setupRegularAuthorization(HttpServletRequest request) {
		if (request.getAttribute("user") != null) {

			GSClientProvider.setCredentials(
					(String) request.getAttribute("user"),
					(String) request.getAttribute("password"));
		}
	}

	protected void setupPublicAuthorization() {
		GSClientProvider.setCredentials(getAdminUsername(), getAdminPassword());
	}

	protected Response processError(Status status, String errorName,
			String message) {

		LinkedHashMap<String, Object> errorData = new LinkedHashMap<>();
		errorData.put("type", errorName);
		errorData.put("message", message);

		return Response.status(status).entity(errorData).build();
	}

	protected Response processError(Status status, Exception e) {

		LinkedHashMap<String, Object> errorData = new LinkedHashMap<>();
		errorData.put("type", e.getClass().getSimpleName());
		errorData.put("message", e.getMessage());

		return Response.status(status).entity(errorData).build();
	}

	protected Response processSuccess(Object data) {

		if (data instanceof String) {
			data = JSONConverter.toJSON(data);
		}
		return Response.ok(data).build();
	}

	protected Response processSuccess() {
		LinkedHashMap<String, Object> dummy = new LinkedHashMap<>();

		return Response.ok(dummy).build();
	}

}
