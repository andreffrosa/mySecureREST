package http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ParserImplmentation {

	private static final int METHOD = 0;
	private static final int PATH = 1;
	private static final int REQUEST_HTTP_VERSION = 2;
	
	
	private static final int RESPONSE_HTTP_VERSION = 0;
	private static final int CODE = 1;
	private static final int MESSAGE = 2;

	public ParserImplmentation() {	}


	public static HTTPReply desseralizeReply(InputStream in) throws IOException {

		Map<String, String> headers = new HashMap<>();

		String[] responseStatus = readLine(in).split(" ", 2); // [HttpVersion, Code, Message]	
		headers = readHeaders(in);
		byte[] buffer = extractBody(in, headers);
		
		return new HTTPReply(responseStatus[RESPONSE_HTTP_VERSION], Integer.parseInt(responseStatus[CODE]), responseStatus[MESSAGE], headers, buffer);
	}
	
	public static HTTPRequest desserializeRequest(InputStream in) throws IOException {
		
		Map<String, String> headers ;

		String[] responseStatus = readLine(in).split(" "); // [Method, Path, HttpVersion]	
		
		//Process headers
		headers = readHeaders(in);
		byte[] buffer = extractBody(in, headers);
		
		return new HTTPRequest( responseStatus[METHOD], responseStatus[PATH], responseStatus[REQUEST_HTTP_VERSION], headers, buffer);
		
	}

	private static byte[] extractBody(InputStream in, Map<String, String> headers) throws IOException {
		int size = Integer.parseInt( headers.get("Content-Length") );
		byte[] buffer = new byte[size];

		int n = 0;
		while( n < size){
			n += in.read(buffer, n, size-n);
		}
		return buffer;
	}

	private static Map<String, String> readHeaders(InputStream in) throws IOException {
		
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
    private static String readLine(InputStream in ) throws IOException {

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
