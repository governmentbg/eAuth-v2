package bg.bulsi.ocspclient.client;

import bg.bulsi.ocspclient.exception.OCSPClientException;

import java.security.cert.X509Certificate;

public interface IOCSPClient {

	/**
	 * Check if X509 Certificate is valid by making OSPC request to QES provider who has provided this certificate.
	 *
	 * @param certificate X509Certificate
	 * @return boolean value isCertificateValid
	 * @throws OCSPClientException
	 */
	public boolean isCertificateValid(X509Certificate certificate) throws OCSPClientException;

}
