package rest.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import http.HTTPReply;
import http.HTTPRequest;
import http.Parser;

public class mySecureRestClient {

	SocketFactory socket_factory;
	URL endpoint;

	public mySecureRestClient() throws NoSuchAlgorithmException {
		this.socket_factory = SSLContext.getDefault().getSocketFactory();
	}
	
	public mySecureRestClient(String location) throws UnknownHostException, IOException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		this(SSLContext.getDefault().getSocketFactory(), location);
	}
	
	public mySecureRestClient(SocketFactory socket_factory, String location) throws UnknownHostException, IOException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		this.socket_factory = socket_factory;
		setLocation(location);
	}
	
	private Socket newSocket(String endpoint, int port) throws UnknownHostException, IOException {
				
		Socket socket = this.socket_factory.createSocket(endpoint, port);
		
		if( socket instanceof SSLSocket )
			((SSLSocket) socket).startHandshake();
		
		return socket;
	}
	
	public mySecureRestClient setLocation(String location) throws MalformedURLException {
		
		this.endpoint = new URL(location);
		
		return this;
	}
	
	public Response post(String path, String content_type, byte[] entity) throws UnknownHostException, IOException {
		
		HTTPRequest request = new HTTPRequest("POST", path, "1.0", null, entity, content_type);
		
		System.out.println(new String(request.serialize())); // TEMP
	    
		Socket socket = newSocket(endpoint.getHost(), endpoint.getPort());
		
	    socket.getOutputStream().write(request.serialize());
	    
	    HTTPReply reply = Parser.desseralizeReply(socket.getInputStream());
	    
	    System.out.println(new String(reply.serialize())); // TEMP
	    
	    return new Response(reply);
	}

}
