package test;

import http.MediaType;
import rest.server.AppMarionete;

public class MarioneteTester {

	public static void main(String[] args) throws Exception {
		HelloWorld h = new myHelloWorld();
		
		AppMarionete marionete = new AppMarionete(h);
		
		String body = new String("não desatina");
		
		marionete.invoke("POST", "/dispatcher/login/bina", body.getBytes(), MediaType.TEXT_PLAIN);
	}

}
