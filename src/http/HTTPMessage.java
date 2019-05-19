package http;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import util.ArrayUtil;

public abstract class HTTPMessage {

	protected String version;
	protected Map<String, String> headers;
	protected byte[] body;
	
	protected HTTPMessage() {
	}
	
	protected HTTPMessage(String version, Map<String, String> headers, byte[] body) {
		this.version = version;
		this.headers = headers;
		this.body = body;
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
	
	public String getHeader(String tag) {
		return headers.get(tag);
	}
	
	public void addHeader(String tag, String value) {
		headers.put(tag, value);
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setBody(byte[] body, String type) {
		this.body = body;
		addHeader("Content-Length", "" + body.length);
		addHeader("Content-Type", type);
	}
	
	protected static final String SEPARATOR = "\r\n";
	protected static final String DATE_PATTERN = "Date: " + SEPARATOR;
	protected static final String HEADER_PATTERN = "%: %s" + SEPARATOR;
	
	protected abstract String getFirstLine();
	
	public byte[] serialize() {
		
		StringBuilder request = new StringBuilder();
		
		// Insert first line
		request.append(getFirstLine());
		
		// Insert current time
		request.append(String.format(DATE_PATTERN, new Date().toString()));

		// Insert headers
		for(Entry<String, String> entry : headers.entrySet() ){
			request.append(String.format(HEADER_PATTERN, entry.getKey(), entry.getValue()));
		}

		// Finish header
		request.append(SEPARATOR);
		
		byte[] header = request.toString().getBytes();
		
		// Append body and return
		return ArrayUtil.concat(header, body);
	}
	
}

