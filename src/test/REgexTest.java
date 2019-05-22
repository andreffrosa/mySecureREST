package test;

import java.net.URL;

import util.URI_Utils;

public class REgexTest {

	public static void  main(String[] args) throws Exception {
		String path = "/dispatcher/login/" + URI_Utils.enconde("bina/chupina", "UTF-8") + "?size=3cm";
		
		URI_Utils.splitPath(path);
	}
	
}
