package rest.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import http.HTTPReply;
import http.HTTPRequest;
import http.MediaType;
import rest.Entity;
import rest.RestResponse;

public class mySecureRestServer {

	private AppMarionete marionete;
	private AtomicBoolean closed;
	private ServerSocketFactory factory;
	private int port;

	public mySecureRestServer(int port, Object handler, ServerSocketFactory factory) {
		this.marionete = new AppMarionete(handler);
		this.factory = factory;
		this.closed = new AtomicBoolean(false);
		this.port = port;
	}

	private synchronized ServerSocket getServerSocket() throws IOException {
		return factory.createServerSocket(port);
	}

	public String getAddress() {
		String proto = factory instanceof SSLServerSocketFactory ? "https" : "http";
		return proto + "://0.0.0.0:" + port + "/";
	}

	public void start() {

		// Lança thread que recebe pedidos REST
		new Thread(()-> {

			try {
				ServerSocket server_socket = getServerSocket();

				while(!closed.get()) {
					// Accepts inbound connection
					Socket client_socket = server_socket.accept();

					HTTPReply reply = null;

					try {
						HTTPRequest request = HTTPRequest.deserializeRequest(client_socket.getInputStream()); // TODO: meter para enviar excepções se estiver mal feito

						Object result = this.marionete.invoke(request.getMethod(), request.getPath(), request.getBody(), request.getContentType(), request.getHeaders());

						if(result instanceof RestResponse) {
							reply = ((RestResponse) result).getHTTPReply();
						} else {
							Entry<String, byte[]> e = Entity.serialize(result);

							reply = new HTTPReply("1.0", 200, "OK", null, e.getValue(), e.getKey());
						}
					} catch(MethodNotFoundException e) {
						// Send HTTP 404
						byte[] message = e.getMessage() != null ? e.getMessage().getBytes() : "".getBytes();
						reply = new HTTPReply("1.0", 404, "Not Found", null, message, MediaType.TEXT_PLAIN);
					} catch(Exception e) {
						// Send HTTP 500
						e.printStackTrace();
						byte[] message = e.getMessage() != null ? e.getMessage().getBytes() : "".getBytes();
						reply = new HTTPReply("1.0", 500, "Internal Server Error", null, message, MediaType.TEXT_PLAIN);
					}		

					// Send Reply
					client_socket.getOutputStream().write(reply.serialize());

					client_socket.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		} ).start();
	}


	public void close() {
		closed.set(true);
	}
}
