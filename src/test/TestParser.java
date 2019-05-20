package test;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Map.Entry;

import http.Parser;

public class TestParser {

	public static void main(String[] args) throws MalformedURLException {
		
		String full = "http://localhost:9999/rest/withtoken/config/new/GreenHouse@0,000000,0,000000?proc=hey&prov=hey";
		Map<String, String> params = Parser.parseParamss(full);
		
		//for(Entry<String, String> entry: params.entrySet())
		//	System.out.printf("%s = %s\n", (String) entry.getKey(), (String) entry.getValue());
		
	}
}
