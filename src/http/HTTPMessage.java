package http;

import java.util.Map;

public class HTTPMessage {

	private String method;
	private String path;
	private String version;
	private Map<String, String> headers;
	private byte[] body;
	
	public HTTPMessage() {
		
	}
	
	public String getPath() {
		return path;
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getHTTPVersion() {
		return version;
	}
	
	public int getContentLength() {
		return Integer.parseInt(headers.get("Content-Length"));
	}
	
	public byte[] getBody() {
		return body;
	}
	
	public String getContentType() {
		return headers.get("Content-Type");
	}
	
}
