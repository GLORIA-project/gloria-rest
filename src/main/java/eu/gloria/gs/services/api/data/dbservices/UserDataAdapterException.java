package eu.gloria.gs.services.api.data.dbservices;

import eu.gloria.gs.services.log.action.ActionException;
import eu.gloria.gs.services.log.action.LogAction;

public class UserDataAdapterException extends ActionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserDataAdapterException(LogAction action) {
		super(action);
	}

	public UserDataAdapterException() {
		super();
	}

}
