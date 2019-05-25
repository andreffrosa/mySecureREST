package test;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;

import http.HTTPReply;
import http.HTTPRequest;
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

	private static void f1() throws Exception {
		SSLServerSocket s = getServerSocketFactory();

		SSLSocket c = (SSLSocket) s.accept();

		HTTPRequest request = HTTPRequest.deserializeRequest(c.getInputStream());

		System.out.println(new String(request.serialize()));

		HTTPReply reply = new HTTPReply("1.0", 200, "OK", null, new byte[0], "");

		c.getOutputStream().write(reply.serialize());
	}

	private static SSLServerSocket getServerSocketFactory() throws Exception {
		String ks_password = "SRSC1819";

		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(new FileInputStream("../SRSC-Proj2/configs/fServer/mainDispatcher/mainDispatcher-keystore.pkcs12"), ks_password.toCharArray());

		KeyStore ts = KeyStore.getInstance("PKCS12");
		ts.load(new FileInputStream("../SRSC-Proj2/configs/fServer/mainDispatcher/mainDispatcher-truststore.pkcs12"), ks_password.toCharArray());

		String[] ciphersuites={"TLS_RSA_WITH_AES_256_CBC_SHA256"};
		String[] protocols={"TLSv1.2"};

		boolean authenticate_clients = true;

		return (SSLServerSocket) new CustomSSLServerSocketFactory(ks, ks_password, ts, ciphersuites, protocols, authenticate_clients).createServerSocket(8080);
	}

	private static KeyManager[] getKeyManager(KeyStore ks, String ks_password) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()); // TODO: receber provider?
		//KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509"); // TODO: passar provider?
		kmf.init(ks, ks_password.toCharArray());

		return kmf.getKeyManagers();
	}

	private static TrustManager[] getTrustManager(KeyStore ts) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
		List<X509Certificate> trustedCertificates = new ArrayList<>();

		TrustManagerFactory tmf2 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf2.init(ts);

		for (TrustManager tm : tmf2.getTrustManagers()) {
			if (tm instanceof X509TrustManager)
				trustedCertificates.addAll(Arrays.asList(((X509TrustManager) tm).getAcceptedIssuers()));
		}

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				//System.err.println(certs[0].getSubjectX500Principal());
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				//Thread.dumpStack();
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return trustedCertificates.toArray(new X509Certificate[0]);
			}
		} };

		return trustAllCerts;
	}

}
