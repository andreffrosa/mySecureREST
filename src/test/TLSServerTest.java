package test;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLServerSocketFactory;

import rest.server.mySecureRestServer;
import ssl.CustomSSLServerSocketFactory;

public class TLSServerTest {

	public static void main(String[] args) throws Exception {
		f2();
	}

	private static void f2() throws Exception {
		String ks_password = "SRSC1819";

		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(new FileInputStream("../SRSC-Proj2/configs/fServer/mainDispatcher/mainDispatcher-keystore.pkcs12"), ks_password.toCharArray());

		KeyStore ts = KeyStore.getInstance("PKCS12");
		ts.load(new FileInputStream("../SRSC-Proj2/configs/fServer/mainDispatcher/mainDispatcher-truststore.pkcs12"), ks_password.toCharArray());

		String[] ciphersuites={"TLS_RSA_WITH_AES_256_CBC_SHA256"};
		String[] protocols={"TLSv1.2"};

		boolean authenticate_clients = true;

		SSLServerSocketFactory factory =  new CustomSSLServerSocketFactory(ks, ks_password, ts, ciphersuites, protocols, authenticate_clients);

		mySecureRestServer server = new mySecureRestServer(8080, new myHelloWorld(), factory);

		server.start();

		System.out.println("Server ready!");
	}

}
