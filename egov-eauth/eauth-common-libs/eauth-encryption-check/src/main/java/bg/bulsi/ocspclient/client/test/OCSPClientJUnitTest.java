package bg.bulsi.ocspclient.client.test;

import bg.bulsi.ocspclient.client.IOCSPClient;
import bg.bulsi.ocspclient.client.OCSPClient;
import bg.bulsi.ocspclient.common.CertificateUtls;
import bg.bulsi.ocspclient.common.OCSPClientConstants;
import bg.bulsi.ocspclient.common.QESProviders;
import bg.bulsi.ocspclient.exception.OCSPClientException;
import org.bouncycastle.ocsp.OCSPException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;


import java.security.cert.X509Certificate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class OCSPClientJUnitTest {

	
	private IOCSPClient ocspClient;
	
	/**
	 *
	 * @throws Exception
	 */
	@Before
	public static void setUpBeforeClass() throws Exception {
		System.setProperty(OCSPClientConstants.CONFIGURATION_FILE_PATH, "C:\\MTITS_Applications\\OCSPClient\\Configuration\\OCSPClientConfiguration.properties");
	}


	/**
	 *
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}


	/**
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		ocspClient = new OCSPClient();
	}


	/**
	 *
	 * @throws Exception
	 */
	@AfterClass
	public void tearDown() throws Exception {
		
	}
	
	
	@Test
	public void testOCSPClient() throws OCSPClientException, OCSPException {
		X509Certificate certificate = CertificateUtls.createTestCertificateFromProvider(QESProviders.B_TRUST);
		boolean isCertificateValid = ocspClient.isCertificateValid(certificate);
		assertTrue("Test certificate of BTrust is VALID", isCertificateValid);
		
		certificate = CertificateUtls.createTestCertificateFromProvider(QESProviders.INFO_NOTARY);
		isCertificateValid = ocspClient.isCertificateValid(certificate);
		assertTrue("Test certificate of INFO_NOTARY is VALID", isCertificateValid);

		certificate = CertificateUtls.createTestCertificateFromProvider(QESProviders.SPEKTAR);
		isCertificateValid = ocspClient.isCertificateValid(certificate);
		assertTrue("Test certificate of SPEKTAR is VALID", isCertificateValid);
		
		certificate = CertificateUtls.createTestCertificateFromProvider(QESProviders.STAMP_IT);
		isCertificateValid = ocspClient.isCertificateValid(certificate);
		assertFalse("Test certificate of INFO_NOTARY is INVALID", isCertificateValid);
		
		String revokedCertificatePath = "C:\\MTITS_Applications\\EAuthenticator\\Resources\\certificates\\testCertificates\\InfoNotary_revoked.cer";
		certificate = CertificateUtls.createX509CertificateByFilePath(revokedCertificatePath);
		isCertificateValid = ocspClient.isCertificateValid(certificate);
		assertFalse("This REVOKED test certificate from INFO_NOTARY is INVALID", isCertificateValid);
		
//		String stampItCertificatePath = "C:\\MTITS_Applications\\OCSPClient\\Resources\\test_certificates\\Elin-Kolev-1-Valid.cer";
//		certificate = CertificateUtls.createX509CertificateByFilePath(stampItCertificatePath);
//		isCertificateValid = ocspClient.isCertificateValid(certificate);
//		assertTrue("This test certificate from StampIt of Elin Kolev is VALID", isCertificateValid);
		
		String stampItCertificateRevokedPath = "C:\\MTITS_Applications\\OCSPClient\\Resources\\test_certificates\\Elin-Kolev-2-temp-revoc.cer";
		certificate = CertificateUtls.createX509CertificateByFilePath(stampItCertificateRevokedPath);
		isCertificateValid = ocspClient.isCertificateValid(certificate);
		
		assertFalse("This test REVOKED certificate from StampIt of Elin Kolev must INVALID", isCertificateValid);
	}
	
}
