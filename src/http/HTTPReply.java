package http;

import java.util.Map;

public class HTTPReply extends HTTPMessage {
	
	private static final int RESPONSE_HTTP_VERSION = 0;
	private static final int CODE = 1;
	private static final int MESSAGE = 2;

	private static final String REPLY_PATTERN = "HTTP/%s %d %s" + SEPARATOR;
	
	private int status_code;
	private String status_message;
	
	public HTTPReply(String version, int status_code, String status_message,  Map<String, String> headers, byte[] body, String type) {
		super(version, headers, body, type);
		this.status_code = status_code;
		this.status_message = status_message;
	}
	
	@Override
	protected String getFirstLine() {
		return String.format(REPLY_PATTERN, version, status_code, status_message).toString();
	}
	
	public int getStatusCode() {
		return status_code;
	}
	
	public String getStatusMessage() {
		return status_message;
	}
	
	public void setStatusCode(int status_code) {
		this.status_code = status_code;
	}
	
	public void setStatusMessage(String status_message) {
		this.status_message = status_message;
	}
	
	public static HTTPReply desseralizeReply(InputStream in) throws IOException {

		Map<String, String> headers = new HashMap<>();

		String[] responseStatus = readLine(in).split(" ", 3); // [HttpVersion, Code, Message]	

		headers = readHeaders(in);

		byte[] buffer = extractBody(in, headers);

		return new HTTPReply(responseStatus[RESPONSE_HTTP_VERSION].replace("HTTP/", ""),
				     Integer.parseInt(responseStatus[CODE]),
				     responseStatus[MESSAGE], 
				     headers, buffer, headers.get("Content-Type"));
	}

}
