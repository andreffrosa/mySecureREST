package rest;

import java.util.AbstractMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import http.MediaType;

public class Entity {

	public static Entry<String,byte[]> serialize(Object entity) {
		String content_type = null;
		byte[] body = null;

		if(entity == null) {
			content_type = ""; // Assim?
			body = new byte[0];
		} else if(entity instanceof String) {
			content_type = MediaType.TEXT_PLAIN;
			body = ((String)entity).getBytes();
		} else if(entity instanceof byte[]) {
			content_type = MediaType.APPLICATION_OCTET_STREAM;
			body = (byte[]) entity; //TODO: Base64? ou vai direto?
		} else {
			Gson gson = new GsonBuilder().create();
			content_type = MediaType.APPLICATION_JSON;
			body = gson.toJson(entity).getBytes();
		}

		return new AbstractMap.SimpleEntry<String, byte[]>(content_type, body);	
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(String content_type, byte[] body, Class<T> entity_class) {
		if(body.length == 0) {
		    return null;		
		}		
		
		switch(content_type) {
			case "":
			case MediaType.TEXT_PLAIN:
				return (T) new String(body);
			case MediaType.APPLICATION_OCTET_STREAM:
				return (T) body;
			case MediaType.APPLICATION_JSON:
				Gson gson = new GsonBuilder().create();
				String json = new String(body);
				return gson.fromJson(json, entity_class);
			default:
				throw new RuntimeException("Unsuported content type!");
		}
	}	
	
}
