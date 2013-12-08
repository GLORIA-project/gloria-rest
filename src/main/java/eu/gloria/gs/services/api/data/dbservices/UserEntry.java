package eu.gloria.gs.services.api.data.dbservices;

import java.util.Date;

public class UserEntry {

	private String name;
	private String password;
	private String roles;
	private String token;
	private Date tokenCreationDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getTokenCreationDate() {
		return tokenCreationDate;
	}

	public void setTokenCreationDate(Date tokenCreationTime) {
		this.tokenCreationDate = tokenCreationTime;
	}
}
