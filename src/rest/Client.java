package rest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public interface Client {
	
	public Response post(String path, Entity<?> entity) ;
	
	public Response get(String path);
	
	public Response put(String path, Entity<?> entity);
	
	public Response delete(String path);
	

}
