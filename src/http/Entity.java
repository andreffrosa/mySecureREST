package http;

import com.google.gson.Gson;

public class Entity {
	
	private static final Gson gson = new Gson();
	
	private String contentType;
	private byte[] data;
	
	private Entity(byte[] data, String contentType) {
		
		this.data = data;
		this.contentType = contentType;
		
	}
	
	public byte[] getBytes() {
		return data;
	}
	
	public static Entity entity(Object object) {
		
		if(object instanceof String)
			return new Entity( ((String)  object).getBytes(), MediaType.TEXT_PLAIN);
		else if(object instanceof byte[])
			return new Entity((byte[]) object, MediaType.APPLICATION_OCTET_STREAM);
		else
			return new Entity(gson.toJson(object /*, object.getClass()*/).getBytes(), MediaType.APPLICATION_JSON);
	}
	
	
	
}
