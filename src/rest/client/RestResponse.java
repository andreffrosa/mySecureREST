package rest.client;

import http.HTTPReply;

public class RestResponse {

	private HTTPReply http_reply;
	
	public Response(HTTPReply reply) {
		this.http_reply = reply;
	}
	
	public int getStatusCode() {
	    return http_reply.getStatusCode();
	}
	
	public Object getEntity(Class<T> c) {
	    // TODO:
	}
}
