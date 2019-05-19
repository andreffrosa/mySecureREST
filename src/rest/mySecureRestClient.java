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
	URL endpoint;

	public mySecureRestClient() {
	}
	
	public mySecureRestClient(String location) throws UnknownHostException, IOException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		setLocation(location);
	}
	
	private SSLSocket newSocket(String endpoint, int port) throws UnknownHostException, IOException {
				
		SSLSocket socket = (SSLSocket) this.socket_factory.createSocket(endpoint, port);
		
		socket.startHandshake();
		return socket;
	}
	
	public mySecureRestClient setLocation(String location) throws MalformedURLException {
		
		this.endpoint = new URL(location);
		
		return this;
	}
	
	public HTTPReply post(String path, String content_type, byte[] entity) throws UnknownHostException, IOException {
		
		HTTPRequest request = new HTTPRequest("POST", path, "1.0", null, entity, content_type);
		
		System.out.println(new String(request.serialize()));
	    
		SSLSocket socket = newSocket(endpoint.getHost(), endpoint.getPort());
		
	    socket.getOutputStream().write(request.serialize());
	    
	    return Parser.desseralizeReply(socket.getInputStream());
	}

}
