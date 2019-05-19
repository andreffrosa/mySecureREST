package http;

import java.util.Map;

public class HTTPRequest extends HTTPMessage {
	
	private static final String REQUEST_PATTERN = "%s %s HTTP/%s" + SEPARATOR;

	private String method;
	private String path;
	
	public HTTPRequest(String method, String path, String version, Map<String, String> headers, byte[] body, String type) {
		super(version, headers, body, type);
		this.method = method;
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	protected String getFirstLine() {
		return String.format(REQUEST_PATTERN, method, path, version).toString();
	}
	
}
