package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class URI_Utils {

	public static String enconde(String param, String charset) throws UnsupportedEncodingException {
		return URLEncoder.encode(param, charset);
	}
	
	public static String enconde(String param) throws UnsupportedEncodingException {
		return URLEncoder.encode(param, "UTF-8");
	}
	
	public static String decode(String param, String charset) throws UnsupportedEncodingException {
		return URLDecoder.decode(param, charset);
	}
	
	public static String decode(String param) throws UnsupportedEncodingException {
		return URLDecoder.decode(param, "UTF-8");
	}
	
	/*public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
		  final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		  final String[] pairs = url.getQuery().split("&");
		  for (String pair : pairs) {
		    final int idx = pair.indexOf("=");
		    final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
		    if (!query_pairs.containsKey(key)) {
		      query_pairs.put(key, new LinkedList<String>());
		    }
		    final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
		    query_pairs.get(key).add(value);
		  }
		  return query_pairs;
		}*/
	
	public static String[] splitPath(String extended_path) {
		
		if(extended_path == null)
			return null;
		
		int index1 = extended_path.indexOf("?");
		int index2 = extended_path.length();
		
		return new String[] {extended_path.substring(0, index1), extended_path.substring(index1+1, index2)};
	}
	
    public static Map<String, String> parseParams(String query, String charset) throws MalformedURLException, UnsupportedEncodingException{
    	
    	Map<String, String> result = new HashMap<>();
        if (query == null)
            return result;

        String[] params = query.split("&");
        for(String param: params) {
        	String[] paramAndValue = param.split("=");
        	if(paramAndValue.length > 1) 
        		result.put(decode(paramAndValue[0], charset), decode(paramAndValue[1], charset));
        	else
        		result.put(paramAndValue[0], "");
        }
    
    	return result;
    }
}
