package rest;

import java.awt.Container;

import javax.print.attribute.standard.Media;

import com.google.gson.Gson;

public class Entity {

	public static Entry<String,byte[]> serialize(Object entity) {
		String content_type = null;
		byte[] body = null;

		if(entity == null) {
			String content_type = ""; // Assim?
			body = new byte[0];
		} else if(entity instanceof String) {
			String content_type = MediaType.TEXT_PLAIN;
			body = ((String)entity).getBytes();
		} else if(entity instanceof byte[]) {
			String content_type = MediaType.APPLICATION_OCTET_STREAM;
			body = entity; //TODO: Base64? ou vai direto?
		} else {
			Gson gson = new GsonBuilder().create();
			String content_type = MediaType.APPLICATION_JSON;
			body = gson.toJson(object).getBytes();
		}

		return new AbstractMap.SimpleEntry<String, byte[]>(content_type, body);	
	}

	public static T deserialize(String content_type, byte[] body, Class<T> entity_class) {
		if(body.lenght == 0) {
		    return null;		
		}		
		
		switch(content_type) {
			case "":
			case MediaType.TEXT_PLAIN:
				return new String(body);
			case MediaType.APPLICATION_OCTET_STREAM:
				return body;
			case MediaType.APPLICATION_JSON:
				Gson gson = new GsonBuilder().create();
				String json = new String(body);
				return gson.fromJson(json, entity_class);
			default:
				throw new RuntimeException("Unsuported content type!");
		}
	}	
	
}
