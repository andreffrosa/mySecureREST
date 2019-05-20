package http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Parser {

	private static final int METHOD = 0;
	private static final int PATH = 1;
	private static final int REQUEST_HTTP_VERSION = 2;
	
	
	private static final int RESPONSE_HTTP_VERSION = 0;
	private static final int CODE = 1;
	private static final int MESSAGE = 2;

	public Parser() {	}


	public static HTTPReply desseralizeReply(InputStream in) throws IOException {

		Map<String, String> headers = new HashMap<>();

		String[] responseStatus = readLine(in).split(" ", 3); // [HttpVersion, Code, Message]	
		headers = readHeaders(in);
		byte[] buffer = extractBody(in, headers);
		
		return new HTTPReply(responseStatus[RESPONSE_HTTP_VERSION].replace("HTTP/", ""), Integer.parseInt(responseStatus[CODE]), responseStatus[MESSAGE], headers, buffer, headers.get("Content-Type"));
	}
	
	public static HTTPRequest desserializeRequest(InputStream in) throws IOException {
		
		Map<String, String> headers ;

		String[] responseStatus = readLine(in).split(" "); // [Method, Path, HttpVersion]	
		
		//Process headers
		headers = readHeaders(in);
		byte[] buffer = extractBody(in, headers);
		
		return new HTTPRequest( responseStatus[METHOD], responseStatus[PATH], responseStatus[REQUEST_HTTP_VERSION].replace("HTTP/", ""), headers, buffer, headers.get("Content-Type"));
		
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
			String[] ans = headerString.split(":");
			headers.put(ans[0].trim() , ans[1].trim());
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
    
    public static Map<String, String> parseParamss(String fullUrl){
    	
    	Map<String, String> result = new HashMap<>();
        if (fullUrl == null)
            return result;

        int last = 0, next, l = fullUrl.length();
        while (last < l) {
            next = fullUrl.indexOf('&', last);
            if (next == -1)
                next = l;

            if (next > last) {
                int eqPos = fullUrl.indexOf('=', last);
                try {
                    if (eqPos < 0 || eqPos > next)
                        result.put(URLDecoder.decode(fullUrl.substring(last, next), "utf-8"), "");
                    else
                        result.put(URLDecoder.decode(fullUrl.substring(last, eqPos), "utf-8"), URLDecoder.decode(fullUrl.substring(eqPos + 1, next), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e); // will never happen, utf-8 support is mandatory for java
                }
            }
            last = next + 1;
        }
        return result;
    	
    	
    	
    }

}
