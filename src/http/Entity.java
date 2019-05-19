package http;

import com.google.gson.Gson;

public class Entity {

	private static final Gson gson = new Gson();
	
	private byte[] rawData;
	private String contentType;
	
	
	private Entity(byte[] rawData, String contentType ) {
		this.rawData = rawData;
		this.contentType = contentType;
	}
	
	public static Entity entity(Object object) {
		
		if (object instanceof String) 
			return new Entity( ((String) object).getBytes(), MediaType.TEXT_PLAIN); 			
		else if(object instanceof byte[])
			return new Entity ( (byte[]) object, MediaType.APPLICATION_OCTET_STREAM);
		else
			return new Entity( gson.toJson(object).getBytes(), MediaType.APPLICATION_JSON);
	}
	
	
	
}
