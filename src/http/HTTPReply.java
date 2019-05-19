package http;

import java.util.Map;

public class HTTPReply extends HTTPMessage {

	private static final String REPLY_PATTERN = "HTTP/%s %d %s" + SEPARATOR;
	
	private int status_code;
	private String status_message;
	
	public HTTPReply(String version, int status_code, String status_message,  Map<String, String> headers, byte[] body) {
		super(version, headers, body);
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

}
