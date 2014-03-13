/**
 * Author: Fernando Serena (fserena@ciclope.info)
 * Organization: Ciclope Group (UPM)
 * Project: GLORIA
 */
package eu.gloria.gs.services.api.resources;

import java.io.IOException;
import java.util.Collection;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

/**
 * @author Fernando Serena (fserena@ciclope.info)
 * 
 */
public class JSONConverter {

	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
	}

	@SuppressWarnings("unchecked")
	public static Object fromJSON(String str, Class<?> cl, Class<?> el) {

		try {

			Object value = null;

			if (str == null) {
				return cl.cast(value);
			}

			if (el == null) {
				value = mapper.readValue(str, cl);
			} else {
				JavaType type = mapper.getTypeFactory()
						.constructCollectionType(
								(Class<? extends Collection<?>>) cl, el);

				value = mapper.readValue(str, type);
			}

			return value;
		} catch (JsonGenerationException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}

		return str;
	}

	public static String toJSON(Object obj) {

		try {
			String json = mapper.writeValueAsString(obj);

			return json;
		} catch (JsonGenerationException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}

		return null;
	}
}
