package http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HTTPRequest extends HTTPMessage {
	
	private static final int METHOD = 0;
	private static final int PATH = 1;
	private static final int REQUEST_HTTP_VERSION = 2;
	
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
	
	public static HTTPRequest deserializeRequest(InputStream in) throws IOException {

		Map<String, String> headers ;

		String[] responseStatus = readLine(in).split(" "); // [Method, Path, HttpVersion]	

		//Process headers
		headers = readHeaders(in);

		byte[] buffer = extractBody(in, headers);

		return new HTTPRequest( responseStatus[METHOD], 
				        responseStatus[PATH], 
				        responseStatus[REQUEST_HTTP_VERSION].replace("HTTP/", ""), 
				        headers, buffer, headers.get("Content-Type"));
	}
	
}
