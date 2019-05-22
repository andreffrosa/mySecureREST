package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class URI_Utils {

	public static String enconde(String param, String charset) throws UnsupportedEncodingException {
		return URLEncoder.encode(param, charset);
	}
	
	public static String enconde(String param) throws UnsupportedEncodingException {
		return URLEncoder.encode(param, "UTF-8");
	}
	
	public static String[] splitPath(String path_with_query_params) {
		// TODO
		return null;
	}
	
	public static List<String> parseQueryParams(String query_params_string) {
		// TODO
		return null;
	}
}
