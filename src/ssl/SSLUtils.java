package ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLUtils {

	public static KeyManager[] getKeyManager(KeyStore ks, String ks_password) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
		return getKeyManager(ks, ks_password, KeyManagerFactory.getDefaultAlgorithm());
	}
	
	public static KeyManager[] getKeyManager(KeyStore ks, String ks_password, String algorithm) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
		kmf.init(ks, ks_password.toCharArray());
		return kmf.getKeyManagers();
	}

	public static TrustManager[] getTrustManager(KeyStore ts) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
		return getTrustManager(ts, TrustManagerFactory.getDefaultAlgorithm());
	}
	
	public static TrustManager[] getTrustManager(KeyStore ts, String algorithm) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
		List<X509Certificate> trustedCertificates = new ArrayList<>();

		TrustManagerFactory tmf2 = TrustManagerFactory.getInstance(algorithm);
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
