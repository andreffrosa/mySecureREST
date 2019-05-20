package rest.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ServerSocketFactory;

import com.google.gson.Gson;

import http.Entity;
import http.HTTPReply;
import http.HTTPRequest;
import http.Parser;
import test.Human;

public class mySecureRestServer {
	
	private static final Gson gson = new Gson();
	
	private Object handler;
	private AtomicBoolean closed;
	private ServerSocketFactory factory;
	private int port;

	public mySecureRestServer(int port, Object handler, ServerSocketFactory factory) {
		this.handler = handler;
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
					System.out.println("U");
					Socket client_socket = server_socket.accept();
					
					HTTPRequest request = Parser.desserializeRequest(client_socket.getInputStream());
					
					System.out.println(new String(request.serialize()));
					
					System.out.println(request.getPath());
					
					
					byte[] data_ = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
					Entity ent = new Entity(request.getBody(), request.getContentType());
					
					Human leitao = (Human) ent.readEntity(Human.class);
					System.out.println("Arrays iguais? " + Arrays.equals(data_, leitao.getBytes()));
					
					//System.out.println( new String( (byte[]) ent.readEntity(null)) );				
					
					HTTPReply reply = new HTTPReply("1.0", 200, "OK", null, new byte[0], "");
					
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
