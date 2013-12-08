package eu.gloria.gs.services.api.data;

import java.util.Date;

import eu.gloria.gs.services.api.data.dbservices.UserDataAdapterException;
import eu.gloria.gs.services.api.data.dbservices.UserDataService;
import eu.gloria.gs.services.api.data.dbservices.UserEntry;

public class UserDataAdapter {

	private UserDataService userService;
	private static SessionIdentifierGenerator tokenizer = new SessionIdentifierGenerator();
	
	public UserDataAdapter() {

	}
	
	public void setUserDataService(UserDataService service) {
		this.userService = service;
		userService.create();
	}

	public String createToken(String name, String password) throws UserDataAdapterException {

		String token = tokenizer.nextSessionId();
		UserEntry entry = new UserEntry();
		
		if (!this.contains(name)) {
			entry.setName(name);
			entry.setPassword(password);
			entry.setToken(token);
			entry.setTokenCreationDate(new Date());
			this.userService.save(entry);
		} else {
			this.userService.setToken(name, token);
			userService.setTokenCreationDate(name, new Date());
		}
		
		return token;
	}
		
	public boolean contains(String name) throws UserDataAdapterException {

		boolean contained = false;

		UserEntry entry = userService.get(name);
		contained = entry != null;

		return contained;
	}

	public UserEntry getUserInformation(String name)
			throws UserDataAdapterException {

		
		if (this.contains(name)) {
			
			return this.userService.get(name);
		}

		return null;
	}
	
	public UserEntry getUserInformationByToken(String token)
			throws UserDataAdapterException {

		UserEntry entry = this.userService.getByToken(token);
		
		if (entry != null) {
			return entry;
		}
		
		throw new UserDataAdapterException("The token does not exist");
	}
	
	public void updateLastCreationDate(String name)
			throws UserDataAdapterException {

		try {
			this.userService.setTokenCreationDate(name, new Date());
		} catch (Exception e) {
			throw new UserDataAdapterException("Error updating token date");
		}
	}

}
