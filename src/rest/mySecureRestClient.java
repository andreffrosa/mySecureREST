package rest;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import http.HTTPReply;
import http.HTTPRequest;
import http.Parser;

public class mySecureRestClient {

	SSLSocketFactory socket_factory;
	SSLSocket socket;
	
	public mySecureRestClient() {
	}
	
	public mySecureRestClient(String location) throws UnknownHostException, IOException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		setLocation(location);
	}
	
	private SSLSocket newSocket(String endpoint, int port) throws UnknownHostException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyStoreException, KeyManagementException {
		SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory.getDefault();
		
		SSLContext ctx;
		KeyManagerFactory kmf;
		KeyStore ks, ts;

		ctx = SSLContext.getInstance("TLS");
		kmf = KeyManagerFactory.getInstance("SunX509");
		ks = KeyStore.getInstance("PKCS12");

		ks.load(new FileInputStream("../SRSC-Proj2/configs/client/client-keystore.pkcs12"), "SRSC1819".toCharArray());
		
		ts = KeyStore.getInstance("PKCS12");
		ts.load(new FileInputStream("../SRSC-Proj2/configs/client/client-truststore.pkcs12"), "SRSC1819".toCharArray());

		kmf.init(ks, "SRSC1819".toCharArray());
		ctx.init(getKeyManager(ks, "SRSC1819"), getTrustManager(ts), null);

		f = ctx.getSocketFactory();
		
		SSLSocket socket = (SSLSocket) f.createSocket(endpoint, port);
		
		socket.startHandshake();
		return socket;
	}
	
	public mySecureRestClient setLocation(String location) throws UnknownHostException, IOException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		URL location_url = new URL(location);
		
		this.socket = newSocket(location_url.getHost(), location_url.getPort());
		
		return this;
	}
	
	public HTTPReply post(String path, String content_type, byte[] entity) throws IOException {
		
		HTTPRequest request = new HTTPRequest("POST", path, "1.0", null, entity, content_type);
		
		System.out.println(new String(request.serialize()));
	     
	    socket.getOutputStream().write(request.serialize());
	    
	    return Parser.desseralizeReply(socket.getInputStream());
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
