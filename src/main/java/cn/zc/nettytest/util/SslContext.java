package cn.zc.nettytest.util;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import io.netty.buffer.ByteBufAllocator;

/**
 * 
 * @author zero
 *
 *
 *         1)生成证书
 * 
 *         a. 在命令行下执行：
 * 
 *         %Java_home%\bin\keytool -genkey -alias tomcat -keyalg RSA
 */
public class SslContext {
	boolean isclient;
	private SSLEngine clientEngine; // client Engine
	private SSLEngine serverEngine; // server Engine
	private SSLContext sslc;

	/*
	 * The following is to set up the keystores.
	 * 双向ssl
	 */
	private static String keyStoreFile = "testkeys";
	private static String trustStoreFile = "testkeys";
	private static String passwd = "passphrase";

	public SslContext(boolean isclient, String keyStoreFile, String trustStoreFile, String passwd) throws Exception {
		super();
		this.isclient = isclient;
		if (keyStoreFile != null) {
			SslContext.keyStoreFile = keyStoreFile;
		}
		if (trustStoreFile != null) {
			SslContext.trustStoreFile = trustStoreFile;
		}
		if (passwd != null) {
			SslContext.passwd = passwd;
		}

		KeyStore ks = KeyStore.getInstance("JKS");
		KeyStore ts = KeyStore.getInstance("JKS");

		char[] passphrase = SslContext.passwd.toCharArray();

		ks.load(new FileInputStream(SslContext.keyStoreFile), passphrase);
		ts.load(new FileInputStream(SslContext.trustStoreFile), passphrase);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, passphrase);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ts);

		SSLContext sslCtx = SSLContext.getInstance("TLS");

		sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		sslc = sslCtx;

		createSSLEngines();
	}

	public SSLEngine newEngine(ByteBufAllocator alloc) {
		// TODO Auto-generated method stub

		if (isclient) {
			return clientEngine;
		}
		return serverEngine;

	}

	/*
	 * Using the SSLContext created during object creation, create/configure the
	 * SSLEngines we'll use for this demo.
	 */
	private void createSSLEngines() throws Exception {
		/*
		 * Configure the serverEngine to act as a server in the SSL/TLS
		 * handshake. Also, require SSL client authentication.
		 */
		serverEngine = sslc.createSSLEngine();
		serverEngine.setUseClientMode(false);
		serverEngine.setNeedClientAuth(true);

		/*
		 * Similar to above, but using client mode instead.
		 */
		clientEngine = sslc.createSSLEngine("client", 80);
		clientEngine.setUseClientMode(true);
	}

}
