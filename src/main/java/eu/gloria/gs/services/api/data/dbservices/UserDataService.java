package eu.gloria.gs.services.api.data.dbservices;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

public interface UserDataService {

	public void create();

	public UserEntry get(@Param(value = "name_") String name);
	
	public UserEntry getByToken(@Param(value = "token_") String token);

	public void save(UserEntry entry);

	public boolean contains(@Param(value = "name_") String name);

	public String getPassword(@Param(value = "name_") String name);

	public void setPassword(@Param(value = "name_") String name,
			@Param(value = "password_") String password);

	public void remove(@Param(value = "name_") String name);
	
	public String getToken(@Param(value = "name_") String name);
	
	public void setToken(@Param(value = "name_") String name, @Param(value = "token_") String token);

	public Date getTokenCreationDate(@Param(value = "name_") String name);
	
	public void setTokenCreationDate(@Param(value = "name_") String name, @Param(value = "date_") Date tokenCreationDate);
}
