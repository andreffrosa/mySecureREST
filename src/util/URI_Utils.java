package util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class URI_Utils {

	public static String enconde(String param, String charset) throws UnsupportedEncodingException {
		return URLEncoder.encode(param, charset);
	}
	
	public static String enconde(String param) throws UnsupportedEncodingException {
		return URLEncoder.encode(param, "UTF-8");
	}
	
	public static String[] splitPath(String path_with_query_params) {
		
		if(path_with_query_params == null)
			return null;
		
		String[] parts = path_with_query_params.split("?", 1);
		 
		if(parts.length > 1)
			return parts;
		else
			return new String[] {parts[0], ""};
		
	}
	
    public static Map<String, String> parseParams(String query) throws MalformedURLException{
    	
    	Map<String, String> result = new HashMap<>();
        if (query == null)
            return result;

        String[] params = new URL(query).getQuery().split("&");
        for(String param: params) {
        	String[] paramAndValue = param.split("=");
        	if(paramAndValue.length > 1) 
        		result.put(paramAndValue[0], paramAndValue[1]);
        	else
        		result.put(paramAndValue[0], "");
        		
        }
    
    	return result;
    }
}
