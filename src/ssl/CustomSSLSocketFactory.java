package ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class CustomSSLSocketFactory extends SSLSocketFactory {

	private final SSLSocketFactory sslSocketFactory;

	public CustomSSLSocketFactory(KeyStore ks, String ks_password, KeyStore ts) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		this.sslSocketFactory = getSocketFactory(ks, ks_password, ts);
	}
	
	private SSLSocketFactory getSocketFactory(KeyStore ks, String ks_password, KeyStore ts) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(SSLUtils.getKeyManager(ks, ks_password), SSLUtils.getTrustManager(ts), null);

		return ctx.getSocketFactory();
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return sslSocketFactory.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return sslSocketFactory.getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslSocketFactory.createSocket();
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
		return sslSocketFactory.createSocket(socket, host, port, autoClose);
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return sslSocketFactory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
		return sslSocketFactory.createSocket(host, port, localHost, localPort);
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return sslSocketFactory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return sslSocketFactory.createSocket(address, port, localAddress, localPort);
	}
	
}

