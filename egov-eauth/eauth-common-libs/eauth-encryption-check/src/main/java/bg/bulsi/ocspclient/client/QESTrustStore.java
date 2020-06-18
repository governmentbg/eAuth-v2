package bg.bulsi.ocspclient.client;

import bg.bulsi.ocspclient.common.OCSPClientConfiguration;
import bg.bulsi.ocspclient.common.QESProviders;
import bg.bulsi.ocspclient.exception.OCSPClientConfigurationException;
import bg.bulsi.ocspclient.exception.QESTrustStoreException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class QESTrustStore implements IQESTrustStore {

	private static final String CA_CERTIFICATE_SUFIX = "CA";
	private static final String OCSP_CERTIFICATE_SUFIX = "OCSP";
	
	private static KeyStore qesTrustStore = null;
	
	
	public QESTrustStore() {
		try {
			loadKeystore();
		} catch (QESTrustStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public X509Certificate getCaCertifiicateByProvider(QESProviders provider) throws QESTrustStoreException {
		if (qesTrustStore == null) {
			loadKeystore();
		}
		
		String caCertificateAlias = provider.getShortName() + CA_CERTIFICATE_SUFIX;
		X509Certificate certificateCA = getCertificateFromStore(qesTrustStore, caCertificateAlias);
		return certificateCA;
	}
	

	@Override
	public X509Certificate getOcspCertifiicateByProvider(QESProviders provider) throws QESTrustStoreException {
		if (qesTrustStore == null) {
			loadKeystore();
		}
		
		String ocspCertificateAlias = provider.getShortName() + OCSP_CERTIFICATE_SUFIX;
		X509Certificate certificateCA = getCertificateFromStore(qesTrustStore, ocspCertificateAlias);
		return certificateCA;
	}
	
	
	private void loadKeystore() throws QESTrustStoreException {
		
		String keystoreType = null;
		try {
			keystoreType = OCSPClientConfiguration.getTruststoreType();
		} catch (OCSPClientConfigurationException e) {
			throw new QESTrustStoreException("Can not find keystoreType from configuration!", e);
		}
		
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(keystoreType);
		} catch (KeyStoreException e) {
			throw new QESTrustStoreException("Can not initialize KeyStore by Type!", e);
		}
		
		
		String keystoreFilePath = null;
		try {
			keystoreFilePath = OCSPClientConfiguration.getTruststoreFilePath();
		} catch (OCSPClientConfigurationException e) {
			throw new QESTrustStoreException("Can not find keystoreFilePath from configuration!", e);
		}
		
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(keystoreFilePath);
		} catch (FileNotFoundException e) {
			throw new QESTrustStoreException("Can not initialize KeyStore by it's JKS file!", e);
		}
		

		String keystorePass = null;
		try {
			keystorePass = OCSPClientConfiguration.getTruststorePassword();
		} catch (OCSPClientConfigurationException e) {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e1) {
					
				}
			}
			
			throw new QESTrustStoreException("Can not find keystorePass from configuration!", e);
		}
		
		try {
			keyStore.load(fileInputStream, keystorePass.toCharArray());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new QESTrustStoreException("Can not initialize KeyStore - wrong algorithm!", e);
		
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new QESTrustStoreException("Can not initialize KeyStore - certificate error!", e);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new QESTrustStoreException("Can not initialize KeyStore - problem with reading the JKS file!", e);
		}
		
		finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		qesTrustStore = keyStore;
	}
	
	
	private X509Certificate getCertificateFromStore(KeyStore keyStore, String certificateAlias) throws QESTrustStoreException {

		Certificate certificate = null;
		try {
			certificate = keyStore.getCertificate(certificateAlias);
		} catch (KeyStoreException e) {
			e.printStackTrace();
			throw new QESTrustStoreException("Can not find certificate for alias " + certificateAlias + " in this KeyStore!", e);
		}
		
		X509Certificate x509Certificate = (X509Certificate) certificate;
		return x509Certificate;
	}

}
