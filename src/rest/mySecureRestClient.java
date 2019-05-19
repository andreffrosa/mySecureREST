package rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

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
