package test;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLSocketFactory;

import http.HTTPReply;
import http.MediaType;
import rest.client.mySecureRestClient;
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
		
		Human leitao = new Human("Joao", "Leitao");
		Human andre = new Human("andre", "rosa");
		leitao.setSon(andre);
		
		String msg = "EHEHEHEHEH grande fdp este agajotina";
		
		
		HTTPReply response = client.post("/path", MediaType.APPLICATION_JSON /*+ "; charset=utf-8"*/, leitao);
	}
	

	

}
