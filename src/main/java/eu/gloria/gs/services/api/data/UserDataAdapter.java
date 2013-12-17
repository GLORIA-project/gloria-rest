package eu.gloria.gs.services.api.data;

import java.util.Date;
import java.util.List;

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

	public String createToken(String name, String password, String locale,
			String userAgent, String remote) throws UserDataAdapterException {

		String token = tokenizer.nextSessionId();
		UserEntry entry = new UserEntry();

		entry.setName(name);
		entry.setPassword(password);
		entry.setToken(token);
		entry.setTokenCreationDate(new Date());
		entry.setTokenUpdateDate(new Date());
		entry.setLocale(locale);
		entry.setRemote(remote);
		entry.setAgent(userAgent);

		this.userService.save(entry);

		return token;
	}

	public void activateToken(String token) {
		userService.setActive(token);
	}

	public void deactivateToken(String token) {
		userService.setInactive(token);
	}

	public void deactivateOtherTokens(String name, String token) {
		userService.setOthersInactive(name, token);
	}

	public boolean containsUser(String name) throws UserDataAdapterException {

		return userService.containsUser(name);
	}

	public boolean containsToken(String name) throws UserDataAdapterException {

		return userService.containsUser(name);
	}

	public List<UserEntry> getUserInformation(String name)
			throws UserDataAdapterException {

		if (this.containsUser(name)) {

			return this.userService.getActive(name);
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

	public void updateLastDate(String token) throws UserDataAdapterException {

		try {
			this.userService.setTokenUpdateDate(token, new Date());
		} catch (Exception e) {
			throw new UserDataAdapterException("Error updating token date");
		}
	}

}
