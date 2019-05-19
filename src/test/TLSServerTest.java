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
import http.Parser;

public class TLSServerTest {

	public static void main(String[] args) throws Exception {
		
		SSLServerSocket s = getServerSocketFactory();

		SSLSocket c = (SSLSocket) s.accept();
		
		HTTPRequest request = Parser.desserializeRequest(c.getInputStream());
		
		System.out.println(new String(request.serialize()));
		
		HTTPReply reply = new HTTPReply("1.0", 200, "OK", null, new byte[0], "");
		
		c.getOutputStream().write(reply.serialize());
	}
	
	private static SSLServerSocket getServerSocketFactory() throws Exception {
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(new FileInputStream("../SRSC-Proj2/configs/fServer/mainDispatcher/mainDispatcher-keystore.pkcs12"), "SRSC1819".toCharArray());
			
			KeyStore ts = KeyStore.getInstance("PKCS12");
			ts.load(new FileInputStream("../SRSC-Proj2/configs/fServer/mainDispatcher/mainDispatcher-truststore.pkcs12"), "SRSC1819".toCharArray());
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, "SRSC1819".toCharArray());
			
			String[] confciphersuites={"TLS_RSA_WITH_AES_256_CBC_SHA256"};
	        String[] confprotocols={"TLSv1.2"};
			
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(getKeyManager(ks, "SRSC1819"), getTrustManager(ts), null);
			
			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			SSLServerSocket s 
			= (SSLServerSocket) ssf.createServerSocket(8080);

			s.setEnabledProtocols(confprotocols);
			s.setEnabledCipherSuites(confciphersuites); 
			s.setNeedClientAuth(true);
			
			return s;
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
