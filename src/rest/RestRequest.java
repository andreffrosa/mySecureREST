package rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import http.HTTPReply;
import http.HTTPRequest;
import utility.URI_Utils;

public class RestRequest {

	private String path;
	private Socket socket;
	private Map<String,String> queryParams;
	private Map<String,String> http_headers;

	public RestRequest(Socket socket, String base_path) {
		this.socket = socket;
		this.path = base_path;
		this.queryParams = new HashMap<>();
		this.http_headers = new HashMap<>();
	}

	public RestRequest addHeader(String key, String value) {
		http_headers.put(key, value);
		return this;
	}
	
	// TODO: Fazer para booleans, ints, longs, doubles e arrays e listas
	public RestRequest addPathParam(String param) throws UnsupportedEncodingException {
		this.path += "/" + URI_Utils.encode(param);
		return this;
	}

	// TODO: Fazer para booleans, ints, longs, doubles e arrays e listas
	public RestRequest addQueryParam(String key, String value) throws UnsupportedEncodingException {
		this.queryParams.put(URI_Utils.encode(key), URI_Utils.encode(value));
		return this;
	}

	public RestRequest addHTTPHeader(String name, String value) {
		http_headers.put(name, value);
		return this;
	}
	
	// private?
	private RestResponse http_request(String method, Object entity) throws IOException {
		
			
		// Colocar os queryParams no path
		String query = "";
		for(Entry<String,String> e : this.queryParams.entrySet()) {
			String separator = query.equals("") ? "?" : "&";
			query += separator + e.getKey() + "=" + e.getValue();
		}
		
		// serializar a entity com base no seu tipo -> TODO
		Entry<String,byte[]> serialized_entity = Entity.serialize(entity);
		byte[] http_body = serialized_entity.getValue();
		String content_type = serialized_entity.getKey();

		// Criar HTTPRequest
		HTTPRequest http_request = new HTTPRequest(method, this.path + query, "1.0", http_headers, http_body, content_type);

		// utilizar o socket para enviar request
		this.socket.getOutputStream().write(http_request.serialize());
		// TODO: Forçar um flush?

		// Esperar por uma reply 
		HTTPReply http_reply = HTTPReply.deseralizeReply(socket.getInputStream()); // TODO: passar este método para a class HTTPReply

		// Fechar o socket
		socket.close();

		// Criar uma resposta
		return new RestResponse(http_reply);
	}

	public RestResponse post(Object entity) throws IOException {
		return http_request("POST", entity);
	}

	public RestResponse get() throws IOException {
		return http_request("GET", null);
	}

	public RestResponse get(Object entity) throws IOException {
		return http_request("GET", entity);
	}

	public RestResponse put(Object entity) throws IOException {
		return http_request("PUT", entity);
	}

	public RestResponse delete(Object entity) throws IOException {
		return http_request("DELETE", entity);
	}

}
