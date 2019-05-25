package test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Map.Entry;

import util.URI_Utils;

public class TestParser {

	public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException {
		
		String full = "http://localhost:9999/rest/withtoken/config/new/GreenHouse@0,000000,0,000000?proc=hey&prov=hey";
		
		String[] split = URI_Utils.splitPath(full);
		
		Map<String, String> params = URI_Utils.parseParams(split[1], "UTF-8");
		
		for(Entry<String, String> entry: params.entrySet())
			System.out.printf("%s = %s\n", (String) entry.getKey(), (String) entry.getValue());
		
	}
}
