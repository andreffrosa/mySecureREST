package test;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLSocketFactory;

import rest.client.RestResponse;
import rest.client.mySecureRestClient;
import ssl.CustomSSLSocketFactory;

public class Test {

	public static void main(String[] args) throws Exception {
		String location = "https://localhost:8080/";
		
		String ks_password = "SRSC1819";
		
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(new FileInputStream("../SRSC-Proj2/configs/client/client-keystore.pkcs12"), ks_password.toCharArray());
		
		KeyStore ts = KeyStore.getInstance("PKCS12");
		ts.load(new FileInputStream("../SRSC-Proj2/configs/client/client-truststore.pkcs12"), ks_password.toCharArray());
		
		SSLSocketFactory factory = new CustomSSLSocketFactory(ks, ks_password, ts);
		
		mySecureRestClient client = new mySecureRestClient(factory, location);
		
		RestResponse response = client.newRequest("/dispatcher")
				                      .addPathParam("login")
								      .addPathParam("bina")
								      .addQueryParam("size", "3cm")
								      .post("não desatina");
		
		System.out.println(response.getStatusCode());
		System.out.println(response.getEntity(boolean.class));
		
		response = client.newRequest("/dispatcher")
                  .addPathParam("cp")
			      .addPathParam("bina")
			      .addQueryParam("origin", "dir1/dir2/")
			      .addQueryParam("dest", "dir3/dir4/")
			      .put("");
		
		System.out.println(response.getStatusCode());
		System.out.println(response.getEntity(boolean.class));
	}

}
