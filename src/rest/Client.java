package rest;

import http.Entity;
import http.Response;

public interface Client {
	
	public Response post(String path, Entity entity) ;
	
	public Response get(String path);
	
	public Response put(String path, Entity entity);
	
	public Response delete(String path);
	

}
