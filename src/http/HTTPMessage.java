package http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utility.ArrayUtil;

public abstract class HTTPMessage {

	protected String version;
	protected Map<String, String> headers;
	protected byte[] body;
	
	protected HTTPMessage() {
	}
	
	protected HTTPMessage(String version, Map<String, String> headers, byte[] body, String type) {
		this.version = version;
		this.headers = headers == null ? new HashMap<>() : headers;
		setBody(body, type);
	}
	
	public Map<String, String> getHeaders() {
		return headers;
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
	protected static final String HEADER_PATTERN = "%s: %s" + SEPARATOR;
	
	protected abstract String getFirstLine();
	
	public byte[] serialize() {
		
		StringBuilder request = new StringBuilder();
		
		// Insert first line
		request.append(getFirstLine());
		
		// Insert Current Time
		headers.put("Date", new Date().toString());

		// Insert Headers
		for(Entry<String, String> entry : headers.entrySet() ){
			request.append(String.format(HEADER_PATTERN, entry.getKey(), entry.getValue()));
		}

		// Finish Header
		request.append(SEPARATOR);
		
		byte[] header = request.toString().getBytes();
		
		// Append body and return
		return ArrayUtil.concat(header, body);
	}
	
	protected static byte[] extractBody(InputStream in, Map<String, String> headers) throws IOException {

		int size = Integer.parseInt( headers.get("Content-Length") );

		byte[] buffer = new byte[size];

		int n = 0;
		while( n < size){
			n += in.read(buffer, n, size-n);
		}

		return buffer;
	}

	protected static Map<String, String> readHeaders(InputStream in) throws IOException {

		Map<String, String> headers = new HashMap<String, String>();

		String headerString;

		while( !(headerString = readLine(in)).equals("") ){
			String[] ans = headerString.split(":");

			headers.put(ans[0].trim() , ans[1].trim());
		}

		return headers;
	}

    /**
     * Reads one message from the HTTP header
     */
    protected static String readLine(InputStream in ) throws IOException {
      StringBuffer sb = new StringBuffer() ;

      int c ;
      while( (c = in.read() ) >= 0 ) {
        if( c == '\r' ) continue ;
        if( c == '\n' ) break ;

        sb.append( new Character( (char)c) ) ;
      }

      return sb.toString() ;
    }
	
}

