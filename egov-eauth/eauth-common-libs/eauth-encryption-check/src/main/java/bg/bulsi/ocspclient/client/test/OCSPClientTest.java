package bg.bulsi.ocspclient.client.test;

import bg.bulsi.ocspclient.client.IOCSPClient;
import bg.bulsi.ocspclient.client.OCSPClient;
import bg.bulsi.ocspclient.common.CertificateUtls;
import bg.bulsi.ocspclient.common.OCSPClientConstants;
import bg.bulsi.ocspclient.common.QESProviders;
import bg.bulsi.ocspclient.exception.OCSPClientException;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class OCSPClientTest {
	
	public static void main(String[] args) throws OCSPClientException {
		System.setProperty(OCSPClientConstants.CONFIGURATION_FILE_PATH, 
				"C:\\MTITS_Applications\\OCSPClient\\Configuration\\OCSPClientConfiguration.properties");
		
		OCSPClientTest ocspClientTest = new OCSPClientTest();
		ocspClientTest.test();
	}

	
	private void test() throws OCSPClientException {
//		X509Certificate certificate = createTestCertificate(QESProviders.INFO_NOTARY);
//		X509Certificate certificate = createTestCertificateRevoked();
//		X509Certificate certificate = createTestCertificateSelfSigned();
		X509Certificate certificate = createTestCertificateSelfSignedByContent();
		
		IOCSPClient ocspClient = new OCSPClient();
		boolean isCertificateValid = ocspClient.isCertificateValid(certificate);
		
		if (isCertificateValid) {
			System.out.println("Certificate is VALID");
		} else {
			System.out.println("Certificate is INVALID");
		}
	}
	
	
	@SuppressWarnings("unused")
	private X509Certificate createTestCertificate(QESProviders provider) {
		X509Certificate newCertificate = null;
		try {
			newCertificate = CertificateUtls.createTestCertificateFromProvider(provider);
		} catch (OCSPClientException e) {
			e.printStackTrace();
		}
		return newCertificate;
	}
	
	
	@SuppressWarnings("unused")
	private X509Certificate createTestCertificateRevoked() {
		String revokedCertificatePath = "C:\\MTITS_Applications\\EAuthenticator\\Resources\\certificates\\testCertificates\\InfoNotary_revoked.cer"; 
		
		X509Certificate newCertificate = null;
		try {
			newCertificate = CertificateUtls.createX509CertificateByFilePath(revokedCertificatePath);
		} catch (OCSPClientException e) {
			e.printStackTrace();
		}
		return newCertificate;
	}
	
	
	private X509Certificate createTestCertificateSelfSignedByContent() {
		String certificateContentBase64Encoded = "MIID5jCCA0+gAwIBAgIJAPahVdM2UPibMA0GCSqGSIb3DQEBBQUAMIGpMQswCQYDVQQGEwJVUzER MA8GA1UECBMITWFyeWxhbmQxEjAQBgNVBAcTCUJhbHRpbW9yZTEpMCcGA1UEChMgU2FtcGxlIFNU UyAtLSBOT1QgRk9SIFBST0RVQ1RJT04xFjAUBgNVBAsTDUlUIERlcGFydG1lbnQxFDASBgNVBAMT C3d3dy5zdHMuY29tMRowGAYJKoZIhvcNAQkBFgtzdHNAc3RzLmNvbTAeFw0xMTAyMDkxODM4MTNa Fw0yMTAyMDYxODM4MTNaMIGpMQswCQYDVQQGEwJVUzERMA8GA1UECBMITWFyeWxhbmQxEjAQBgNV BAcTCUJhbHRpbW9yZTEpMCcGA1UEChMgU2FtcGxlIFNUUyAtLSBOT1QgRk9SIFBST0RVQ1RJT04x FjAUBgNVBAsTDUlUIERlcGFydG1lbnQxFDASBgNVBAMTC3d3dy5zdHMuY29tMRowGAYJKoZIhvcN AQkBFgtzdHNAc3RzLmNvbTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAo+f8gs4WcteLdSPW Pm8+ciyEz7zVmA7kcCGFQQvlO0smxRViWJ1x+yniT5Uu86UrAQjxRJyANBomQrirfE7KPrnCm6iV OsGDEntuIZAf7DFPnrv5p++jAZQuR3vm4ZHXFOFTXmI+/FD5AqLfNi17xiTxZCDYyDdD39CNFTrB 2PkCAwEAAaOCARIwggEOMB0GA1UdDgQWBBRa0A38holQIbJMFW7m5ZSw+iVDHDCB3gYDVR0jBIHW MIHTgBRa0A38holQIbJMFW7m5ZSw+iVDHKGBr6SBrDCBqTELMAkGA1UEBhMCVVMxETAPBgNVBAgT CE1hcnlsYW5kMRIwEAYDVQQHEwlCYWx0aW1vcmUxKTAnBgNVBAoTIFNhbXBsZSBTVFMgLS0gTk9U IEZPUiBQUk9EVUNUSU9OMRYwFAYDVQQLEw1JVCBEZXBhcnRtZW50MRQwEgYDVQQDEwt3d3cuc3Rz LmNvbTEaMBgGCSqGSIb3DQEJARYLc3RzQHN0cy5jb22CCQD2oVXTNlD4mzAMBgNVHRMEBTADAQH/ MA0GCSqGSIb3DQEBBQUAA4GBACp9yK1I9r++pyFT0yrcaV1m1Sub6urJH+GxQLBaTnTsaPLuzq2g IsJHpwk5XggB+IDe69iKKeb74Vt8aOe5usIWVASgi9ckqCwdfTqYu6KG9BlezqHZdExnIG2v/cD/ 3NkKr7O/a7DjlbE6FZ4G1nrOfVJkjmeAa6txtYm1Dm/f"; 
		
		X509Certificate certificate = null;
		try {
			certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(Base64.decode(certificateContentBase64Encoded.getBytes())));
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return certificate;
	}
	
	
	@SuppressWarnings("unused")
	private X509Certificate createTestCertificateSelfSigned() {
		String certificatePath = "C:\\MTITS_Applications\\OCSPClient\\Resources\\selfSigned\\selfSigned_1.cer"; 
		
		X509Certificate newCertificate = null;
		try {
			newCertificate = CertificateUtls.createX509CertificateByFilePath(certificatePath);
		} catch (OCSPClientException e) {
			e.printStackTrace();
		}
		return newCertificate;
	}

}
