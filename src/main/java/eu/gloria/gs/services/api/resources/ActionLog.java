/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.spi.resource.Singleton;

import eu.gloria.gs.services.core.client.GSClientProvider;
import eu.gloria.gs.services.log.action.ActionLogException;
import eu.gloria.gs.services.log.action.ActionLogInterface;
import eu.gloria.gs.services.utils.ObjectResponse;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */

@Singleton
@Path("/logs")
public class ActionLog extends GResource {

	@Context
	HttpServletRequest request;

	private static ActionLogInterface log = GSClientProvider.getActionLogClient();

	private Date beginDateOf(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
	
	private Date endDateOf(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		
		return calendar.getTime();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/today")
	public Response getTodaysLog() {
		
		this.setupRegularAuthorization(request);
		
		Date today = new Date();
		
		ObjectResponse response;
		try {
			response = log.getAllDateLogs(beginDateOf(today), endDateOf(today));
			return Response.ok(response.content).build();
		} catch (ActionLogException e) {
			return Response.serverError().build();
		}		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/infos/today")
	public Response getTodaysInfoLog() {
		
		this.setupRegularAuthorization(request);
		
		Date today = new Date();
		
		ObjectResponse response;
		try {
			response = log.getInfoLogsByDate(beginDateOf(today), endDateOf(today));
			return Response.ok(response.content).build();
		} catch (ActionLogException e) {
			return Response.serverError().build();
		}		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/errors/today")
	public Response getTodaysErrorLog() {
		
		this.setupRegularAuthorization(request);
		
		Date today = new Date();
		
		ObjectResponse response;
		try {
			response = log.getErrorLogsByDate(beginDateOf(today), endDateOf(today));
			return Response.ok(response.content).build();
		} catch (ActionLogException e) {
			return Response.serverError().build();
		}		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}")
	public Response getContextLog(@PathParam("rid") int rid) {
		
		this.setupRegularAuthorization(request);
		
		Date today = new Date();
		
		ObjectResponse response;
		try {
			response = log.getAllRidLogsByDate(rid, beginDateOf(today), endDateOf(today));
			return Response.ok(response.content).build();
		} catch (ActionLogException e) {
			return Response.serverError().build();
		}		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/infos")
	public Response getContextInfoLog(@PathParam("rid") int rid) {
		
		this.setupRegularAuthorization(request);
		
		Date today = new Date();
		
		ObjectResponse response;
		try {
			response = log.getInfoRidLogsByDate(rid, beginDateOf(today), endDateOf(today));
			return Response.ok(response.content).build();
		} catch (ActionLogException e) {
			return Response.serverError().build();
		}		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/warnings")
	public Response getContextWarningLog(@PathParam("rid") int rid) {
		
		this.setupRegularAuthorization(request);
		
		Date today = new Date();
		
		ObjectResponse response;
		try {
			response = log.getWarningRidLogsByDate(rid, beginDateOf(today), endDateOf(today));
			return Response.ok(response.content).build();
		} catch (ActionLogException e) {
			return Response.serverError().build();
		}		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/context/{rid}/errors")
	public Response getContextErrorLog(@PathParam("rid") int rid) {
		
		this.setupRegularAuthorization(request);
		
		Date today = new Date();
		
		ObjectResponse response;
		try {
			response = log.getWarningRidLogsByDate(rid, beginDateOf(today), endDateOf(today));
			return Response.ok(response.content).build();
		} catch (ActionLogException e) {
			return Response.serverError().build();
		}		
	}


}
