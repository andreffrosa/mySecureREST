package ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class CustomSSLServerSocketFactory extends SSLServerSocketFactory {

	private final SSLServerSocketFactory sslServerSocketFactory;
	private String[] ciphersuites;
	private String[] protocols;
	boolean authenticate_clients;

	public CustomSSLServerSocketFactory(KeyStore ks, String ks_password, KeyStore ts, String[] ciphersuites, String[] protocols, boolean authenticate_clients) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		this.sslServerSocketFactory = getServerSocketFactory(ks, ks_password, ts);
		this.ciphersuites = ciphersuites;
		this.protocols = protocols;
		this.authenticate_clients = authenticate_clients;
	}
	
	private SSLServerSocketFactory getServerSocketFactory(KeyStore ks, String ks_password, KeyStore ts) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
	
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(SSLUtils.getKeyManager(ks, ks_password), SSLUtils.getTrustManager(ts), null);
		
		return sc.getServerSocketFactory();
	}

	private SSLServerSocket adjustEnabledCipherSuites(SSLServerSocket socket) throws IOException {

		socket.setEnabledCipherSuites(ciphersuites);
		socket.setEnabledProtocols(protocols);
		socket.setNeedClientAuth(authenticate_clients);

		return socket;
	}
	
	@Override
	public String[] getDefaultCipherSuites() {
		return this.ciphersuites;
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return this.ciphersuites;
	}

	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		return adjustEnabledCipherSuites((SSLServerSocket) sslServerSocketFactory.createServerSocket(port));
	}

	@Override
	public ServerSocket createServerSocket(int port, int backlog) throws IOException {
		return adjustEnabledCipherSuites((SSLServerSocket) sslServerSocketFactory.createServerSocket(port, backlog));
	}

	@Override
	public ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress) throws IOException {
		return adjustEnabledCipherSuites((SSLServerSocket) sslServerSocketFactory.createServerSocket(port, backlog, ifAddress));
	}
}

