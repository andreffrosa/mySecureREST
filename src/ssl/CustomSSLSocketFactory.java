package ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class CustomSSLSocketFactory extends SSLSocketFactory {

	private final SSLSocketFactory sslSocketFactory;
	private String[] ciphersuites;
	private String[] protocols;
	boolean authenticate_clients;

	public CustomSSLSocketFactory(SSLSocketFactory sslSocketFactory, String[] ciphersuites, String[] protocols, boolean authenticate_clients) {
		this.sslSocketFactory = sslSocketFactory;
		this.ciphersuites = ciphersuites;
		this.protocols = protocols;
		this.authenticate_clients = authenticate_clients;
	}

	@Override
	public String[] getDefaultCipherSuites() {
		//return sslSocketFactory.getDefaultCipherSuites();
		return this.ciphersuites;
	}

	@Override
	public String[] getSupportedCipherSuites() {
		//return sslSocketFactory.getSupportedCipherSuites();
		return this.ciphersuites;
	}

	@Override
	public Socket createSocket() throws IOException {
		return adjustEnabledCipherSuites((SSLSocket) sslSocketFactory.createSocket());
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
		return adjustEnabledCipherSuites((SSLSocket) sslSocketFactory.createSocket(socket, host, port, autoClose));
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return adjustEnabledCipherSuites((SSLSocket) sslSocketFactory.createSocket(host, port));
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
		return adjustEnabledCipherSuites((SSLSocket) sslSocketFactory.createSocket(host, port, localHost, localPort));
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return adjustEnabledCipherSuites((SSLSocket) sslSocketFactory.createSocket(host, port));
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return adjustEnabledCipherSuites((SSLSocket) sslSocketFactory.createSocket(address, port, localAddress, localPort));
	}

	private SSLSocket adjustEnabledCipherSuites(SSLSocket socket) throws IOException {

		socket.setEnabledCipherSuites(ciphersuites);
		socket.setEnabledProtocols(protocols);
		socket.setNeedClientAuth(authenticate_clients);
		
		if(socket.getRemoteSocketAddress() != null)
			socket.startHandshake();

		return socket;
	}
}

