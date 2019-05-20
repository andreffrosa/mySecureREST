package rest.client;

import http.Entity;
import http.HTTPReply;

public interface Client {
	
	public HTTPReply post(String path, Entity entity) ;
	
	public HTTPReply get(String path);
	
	public HTTPReply put(String path, Entity entity);
	
	public HTTPReply delete(String path);
	

}
