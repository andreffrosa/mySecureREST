package rest.client;

import http.HTTPReply;
import rest.Entity;

public class RestResponse {

	private HTTPReply http_reply;
	
	public RestResponse(HTTPReply reply) {
		this.http_reply = reply;
	}
	
	public int getStatusCode() {
	    return http_reply.getStatusCode();
	}
	
	public <T> T getEntity(Class<T> c) {
	    return Entity.deserialize(http_reply.getContentType(), http_reply.getBody(), c);
	}
	
}
