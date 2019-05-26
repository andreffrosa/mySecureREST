package rest.client;

import java.util.Map;
import java.util.Map.Entry;

import http.HTTPReply;
import rest.Entity;

public class RestResponse {

	private HTTPReply http_reply;
	
	public RestResponse(HTTPReply reply) {
		this.http_reply = reply;
	}
	
	public RestResponse(String version, int status_code, String status_message, Object entity) {
		Entry<String, byte[]> e = Entity.serialize(entity);
		this.http_reply = new HTTPReply(version, status_code, status_message, null, e.getValue(), e.getKey());
	}
	
	public HTTPReply getHTTPReply() {
		return http_reply;
	}
	
	public int getStatusCode() {
	    return http_reply.getStatusCode();
	}
	
	public <T> T getEntity(Class<T> c) {
	    return Entity.deserialize(http_reply.getContentType(), http_reply.getBody(), c);
	}
	
}
