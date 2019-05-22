package rest.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ServerSocketFactory;
import javax.ws.rs.ProcessingException;

import http.HTTPReply;
import http.HTTPRequest;
import http.Parser;

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
		System.out.println("Ya");
		return factory.createServerSocket(port);
	}

	public void start() {

		// Lança thread que recebe pedidos REST
		new Thread(()-> {

			try {
				ServerSocket server_socket = getServerSocket();

				while(!closed.get()) {
					// Accepts inbound connection
					Socket client_socket = server_socket.accept();
	
					int reply_status_code = 0;
					String reply_status_msg = "";
					byte[] reply_body = null;
					String reply_content_type = "";
					
					try {
						HTTPRequest request = Parser.desserializeRequest(client_socket.getInputStream()); // TODO: meter para enviar excepções se estiver mal feito
						
						Object body = new String(request.getBody()); // TODO: deserializar
						
						this.marionete.invoke(request.getMethod(), request.getPath(), body);
						
						reply_status_code = 200;
						reply_status_msg = "OK";
						reply_body = new byte[0];
						
					} catch(ProcessingException e) {
						
					} catch(Exception e) {
						// Send HTTP 500
						reply_status_code = 500;
						reply_status_msg = "Internal Server Error";
						reply_body = new byte[0];
						e.printStackTrace();
					}		
					
					HTTPReply reply = new HTTPReply("1.0", reply_status_code, reply_status_msg, null, reply_body, reply_content_type); // TODO: Acrescentar para quando o body é null criar um array[0] lá dentros
					
					// Send Reply
					client_socket.getOutputStream().write(reply.serialize());
					
					client_socket.close();
					
					System.out.println("AQUI");
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
