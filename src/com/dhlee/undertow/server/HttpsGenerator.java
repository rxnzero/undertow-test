package com.dhlee.undertow.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class HttpsGenerator {

//	public static HostConfiguration getHostConfiguration(String url) {
//
//        if(url.startsWith("https")){
//
//              URI uri;
//              try {
//                     uri = new URI(url,true);
//              } catch (URIException e1) {
//                     return null;
//              } catch (NullPointerException e1) {
//                     return null;
//              }
//
//              Protocol httpsProtocol = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
//              HostConfiguration hc = new HostConfiguration();
//              try {
//                     hc.setHost(uri.getHost(), uri.getPort(), httpsProtocol);
//              } catch (URIException e) {
//                     return null;
//              }
//
//              return hc;
//
//        } else {
//              return null;
//        }
//	}
	
//	public static void setSSLTrust() {
//	
//        Protocol httpsProtocol = new Protocol("https", new SSLProtocolSocketFactory(), 443);
//        Protocol.registerProtocol("https", httpsProtocol);
//	}
	
	public static SSLContext createSSLContext(String keystoreFile, String keystorePass) {
	
        SSLContext sslContext = null;
        try {
				sslContext = SSLContext.getInstance("TLS");
				KeyManager[] keyManagers = createKeyManager(keystoreFile, keystorePass);
				
				// Client Auth 지원하지 않음. - 추후 지원시 Comment 제거
				// TrustManager[] trustManagers = createTrustManager(trustKey,trustPass);
				TrustManager[] trustManagers = null;
				
				if (null == keyManagers) return null;
				
				sslContext.init(keyManagers, trustManagers, null);

        } catch (NoSuchAlgorithmException e) {

//	        	logger.error(e.getMessage(), e);
        	e.printStackTrace();
	        	return null;

        } catch (KeyManagementException e) {

//	        	logger.error(e.getMessage(), e);
        	e.printStackTrace();
	        	return null;
        }

        return sslContext;
	}
	
	public static KeyManager[] createKeyManager(String keystoreFile, String password) {
	
        KeyManager[] keyManagers = null;
        File keyStoreFile = new File(keystoreFile);

        try {
			  FileInputStream keyStoreInputStream =  new FileInputStream(keyStoreFile);
			  KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			  KeyStore keyStore = getKeyStore(keyStoreInputStream,password);
			
			  if (keyStore == null) return null;
			
			  keyManagerFactory.init(keyStore,password.toCharArray());
			  keyManagers = keyManagerFactory.getKeyManagers();

        } catch (NoSuchAlgorithmException e) {

//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;

        } catch (UnrecoverableKeyException e) {

//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;

        } catch (KeyStoreException e) {

//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;

        } catch (FileNotFoundException e) {

//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;
        }

        return keyManagers;
	}
	
	public static TrustManager[] createTrustManager(String keystoreFile,String password) {
	
        if (null == keystoreFile) return null;
        TrustManager[] trustManager = null;

        File keyStoreFile = new File(keystoreFile);

        try {
              FileInputStream keyStoreInputStream =  new FileInputStream(keyStoreFile);
              TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
              KeyStore trustStore = getKeyStore(keyStoreInputStream,password);

              if (trustStore == null) return null;

              trustManagerFactory.init(trustStore);
              trustManager = trustManagerFactory.getTrustManagers();

        } catch (NoSuchAlgorithmException e) {

//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;

        } catch (KeyStoreException e) {

//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;

        } catch (FileNotFoundException e) {

//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;
        }

        return trustManager;
	}
	
	private static KeyStore getKeyStore(InputStream key, String password) {
	
        KeyStore keyStore = null;

        try {
              keyStore = KeyStore.getInstance("JKS");
              keyStore.load(key, password.toCharArray());

        } catch (KeyStoreException e) {

        	e.printStackTrace();
              return null;

        } catch (NoSuchAlgorithmException e) {

//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;

        } catch (IOException e) {

//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;

        } catch (java.security.cert.CertificateException e) {
        	
//        	  logger.error(e.getMessage(), e);
        	e.printStackTrace();
              return null;
		}

        return keyStore;
	}	

}
