package bg.bulsi.ocspclient.client;

import bg.bulsi.ocspclient.checker.Checker;
import bg.bulsi.ocspclient.checker.OCSPResponseDetails;
import bg.bulsi.ocspclient.common.CertificateUtls;
import bg.bulsi.ocspclient.common.OCSPClientConfiguration;
import bg.bulsi.ocspclient.common.OcspClientUtils;
import bg.bulsi.ocspclient.common.QESProviders;
import bg.bulsi.ocspclient.exception.OCSPClientConfigurationException;
import bg.bulsi.ocspclient.exception.OCSPClientException;
import bg.bulsi.ocspclient.exception.QESProvidersException;
import bg.bulsi.ocspclient.exception.QESTrustStoreException;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

public class OCSPClient implements IOCSPClient {

	private QESProviders provider;

	private X509Certificate caCertificate;
	private X509Certificate ocspCertificate;
	
	private String ocspURL = null;

	@Override
	public boolean isCertificateValid(X509Certificate certificate) throws OCSPClientException {
		
		if (certificate == null) {
			String errorMessage = "The certificate must not be NULL!";
			System.err.println(errorMessage);
			
			throw new OCSPClientException(errorMessage);
		}

		/*
		 * Check certificate validity by its period according it is expired of is not active yet.
		 * 
		 */
		boolean toCheckValidityByPeriod = false;
		try {
			toCheckValidityByPeriod = OCSPClientConfiguration.getEnableCheckValidityByPeriod();
		} catch (OCSPClientConfigurationException e) {
			String errorMessage = "Can not find is To Check Validity By Period from configuration!";
			System.err.println(errorMessage);
			e.printStackTrace();
			
			throw new OCSPClientException(errorMessage, e);
		}
		
		if (toCheckValidityByPeriod) {
			boolean isCertificateValidByExpiringPeriod = isCertificateValidByExpiringPeriod(certificate);
			if (!isCertificateValidByExpiringPeriod) {
				return false; 
			}
		}

		String issuerOrganization = CertificateUtls.getIssuerOrganization(certificate);
		
		
		/*
		 * If provider is from a list of SelfSigned Providers then do not make OCSP check!
		 */
		boolean isProviderInSelfSignedProviders = OcspClientUtils.isProviderInSelfSignedProviderList(issuerOrganization); 
		if (isProviderInSelfSignedProviders) {
			System.out.println("Provider '" + issuerOrganization + "' is from SELF_SIGNED_PROVIDER_LIST");
			return true;
		} else {
			System.out.println("Provider '" + issuerOrganization + "' is not from SELF_SIGNED_PROVIDER_LIST");
		}
		
		
		/*
		 * Define certificates for current provider.
		 */
		try {
			provider = QESProviders.getByOrganizationName(issuerOrganization);
		} catch (QESProvidersException e) {
			String errorMessage = "Error obtaining QES provider!";
			System.err.println(errorMessage);
			e.printStackTrace();
			
			throw new OCSPClientException(errorMessage, e);
		}
		

		/*
		 * Defining providers certificates. 
		 */
		try {
			defineProviderCertificates();	
		} catch (OCSPClientException e) {
			String errorMessage = "Error defining providers certificates!";
			System.err.println(errorMessage);
			e.printStackTrace();
			
			throw new OCSPClientException(errorMessage, e);
		}
		
		
		/*
		 * Defining providers OCSP URLs
		 */
		try {
			defineProviderOcspUrl();	
		} catch (OCSPClientException e) {
			String errorMessage = "Error defining providers OCSP URLs!";
			System.err.println(errorMessage);
			e.printStackTrace();
			
			throw new OCSPClientException(errorMessage, e);
		}
		
		
		
		/*
		 * Call OCSPChecker...
		 */
		Checker ocspChecker = new Checker();
		OCSPResponseDetails ocspResponseDetails = null;
		
		try {
			ocspResponseDetails = ocspChecker.getCertificateStatus(certificate, caCertificate, ocspCertificate, ocspURL);
		} catch (Exception e) {
			String errorMessage = "Error maiking OCSP checking!";
			System.err.println(errorMessage);
			e.printStackTrace();
			
			throw new OCSPClientException(errorMessage, e);
		}
				
		ocspResponseDetails.showDetails();

		boolean isQesValid = ocspResponseDetails.isValid();
		return isQesValid;
	}

	
	private void defineProviderOcspUrl() throws OCSPClientException {
		String providerName = provider.getShortName();
		
		try {
			ocspURL = OCSPClientConfiguration.getOcspUrlByProvider(providerName);
		} catch (OCSPClientConfigurationException e) {
			throw new OCSPClientException("Can not find ocspURL for provider " + providerName, e);
		}
		
		if (ocspURL == null) {
			throw new OCSPClientException("OCSP URL for provider " + providerName + " has no value in configuration! Check the configuration or the correctnes of provider name.");
		}
	}


	private void defineProviderCertificates() throws OCSPClientException {
		QESTrustStore trustStore = new QESTrustStore();
		
		try {
			caCertificate = trustStore.getCaCertifiicateByProvider(provider);
		} catch (QESTrustStoreException e) {
			throw new OCSPClientException("Error obtaining CA certificate from TrustStore for provider " + provider.getShortName(), e); 
		}
		
		if (caCertificate == null) {
			throw new OCSPClientException("There is no CA certificate from TrustStore for provider " + provider.getShortName() + ". Check TrustStore content!");
		}
		
		try {
			ocspCertificate = trustStore.getOcspCertifiicateByProvider(provider);
		} catch (QESTrustStoreException e) {
			throw new OCSPClientException("Error obtaining OCSP certificate in TrustStore for provider " + provider.getShortName(), e);
		}
		
		if (ocspCertificate == null) {
			throw new OCSPClientException("There is no OCSP certificate in TrustStore for provider " + provider.getShortName() + ". Check TrustStore content!");
		}

	}


	private boolean isCertificateValidByExpiringPeriod(X509Certificate certificate) {
		boolean isCertificateValidByDatePeriod = true;
		try {
			certificate.checkValidity();
		} catch (CertificateExpiredException e1) {
			isCertificateValidByDatePeriod = false;
		} catch (CertificateNotYetValidException e1) {
			isCertificateValidByDatePeriod = false;
		}
		
		return isCertificateValidByDatePeriod;
	}

}
