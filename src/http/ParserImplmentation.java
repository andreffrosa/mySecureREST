package http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ParserImplmentation implements Parser {

	private static final int METHOD = 0;
	private static final int PATH = 1;
	private static final int REQUEST_HTTP_VERSION = 2;
	
	
	private static final int RESPONSE_HTTP_VERSION = 0;
	private static final int CODE = 1;
	private static final int MESSAGE = 2;

	public ParserImplmentation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] serialize(HTTPMessage httpMessage) {

		return null;
	}

	@Override
	public HttpReply desseralizeReply(InputStream in) {

		Map<String, String> headers = new HashMap<>();
		byte[] content;

		String[] responseStatus = readLine(in).split(" ", 2); // [HttpVersion, Code, Message]	
		headers = readHeaders(in);
		byte[] buffer = extractData(in, headers);
		
		return new HttpReply(responseStatus[RESPONSE_HTTP_VERSION], responseStatus[CODE], responseStatus[MESSAGE], headers, buffer);
	}
	
	public HTTPRequest desserializeRequest(InputStream in) {
		
		Map<String, String> headers ;
		byte[] content;

		String[] responseStatus = readLine(in).split(" ", 2); // [HttpVersion, Code, Message]	
		
		//Process headers
		headers = readHeaders(in);
		byte[] buffer = extractData(in, headers);
		
		return new HTTPRequest(responseStatus[RESPONSE_HTTP_VERSION], responseStatus[METHOD], responseStatus[PATH], headers, buffer);
		
	}

	private byte[] extractData(InputStream in, Map<String, String> headers) throws IOException {
		int size = Integer.parseInt( headers.get("Content-Length") );
		byte[] buffer = new byte[size];

		int n = 0;
		while( n < size){
			n += in.read(buffer, n, size-n);
		}
		return buffer;
	}

	private Map<String, String> readHeaders(InputStream in) throws IOException {
		
		Map<String, String> headers = new HashMap<String, String>();
		String headerString;
		while( !(headerString = readLine(in)).equals("") ){
			String[] ans = headerString.split(": ");
			headers.put(ans[0] , ans[1]);
		}
		
		return headers;
	}
	
    /**
     * Reads one message from the HTTP header
     */
    private String readLine(InputStream in ) throws IOException {

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
