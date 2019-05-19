package test;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLSocketFactory;

import rest.Response;
import rest.mySecureRestClient;
import ssl.CustomSSLSocketFactory;

public class Test {

	public static void main(String[] args) throws Exception {
		String location = "https://localhost:8080/";
		
		String ks_password = "SRSC1819";
		
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(new FileInputStream("../SRSC-Proj2/configs/fServer/mainDispatcher/mainDispatcher-keystore.pkcs12"), ks_password.toCharArray());
		
		KeyStore ts = KeyStore.getInstance("PKCS12");
		ts.load(new FileInputStream("../SRSC-Proj2/configs/fServer/mainDispatcher/mainDispatcher-truststore.pkcs12"), ks_password.toCharArray());
		
		SSLSocketFactory factory = new CustomSSLSocketFactory(ks, ks_password, ts);
		
		mySecureRestClient client = new mySecureRestClient(factory, location);
		
		Response response = client.post("/path", "text/html; charset=utf-8", "ola".getBytes());
	}
	
	

}
