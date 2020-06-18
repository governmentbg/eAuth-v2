package bg.bulsi.ocspclient.client;

import bg.bulsi.ocspclient.common.QESProviders;
import bg.bulsi.ocspclient.exception.QESTrustStoreException;

import java.security.cert.X509Certificate;

public interface IQESTrustStore {

	public X509Certificate getCaCertifiicateByProvider(QESProviders provider) throws QESTrustStoreException;
	
	public X509Certificate getOcspCertifiicateByProvider(QESProviders provider) throws QESTrustStoreException;

}
