package test;

import java.net.InetAddress;
import java.net.URL;

import http.HTTPReply;
import rest.mySecureRestClient;

public class Test {

	public static void main(String[] args) throws Exception {
		String location = "https://localhost:8080/";
		
		//System.setProperty("javax.net.ssl.trustStrore", "../SRSC-Proj2/configs/client/client-truststore.pkcs12");
		
		mySecureRestClient client = new mySecureRestClient(location);
		
		HTTPReply reply = client.post("/", "text/html; charset=utf-8", "ola".getBytes());
		
		System.out.println(new String(reply.serialize()));
		
		System.out.println(reply.getHTTPVersion());
	}
	
	

}
