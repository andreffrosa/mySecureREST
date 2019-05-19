package http;

import java.util.Map;

public class HTTPReply extends HTTPMessage {

	private static final String REPLY_PATTERN = "" + SEPARATOR;
	
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

}
