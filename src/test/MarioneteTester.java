package test;

import rest.server.AppMarionete;

public class MarioneteTester {

	public static void main(String[] args) throws Exception {
		HelloWorld h = new myHelloWorld();
		
		AppMarionete marionete = new AppMarionete(h);
		
		Object body = new String("não empina");
		
		marionete.invoke("POST", "/dispatcher/login/bina", body);
	}

}
