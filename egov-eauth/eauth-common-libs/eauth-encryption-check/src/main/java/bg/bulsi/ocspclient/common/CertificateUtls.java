package bg.bulsi.ocspclient.common;

import bg.bulsi.ocspclient.exception.OCSPClientConfigurationException;
import bg.bulsi.ocspclient.exception.OCSPClientException;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.X509Name;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Vector;

@SuppressWarnings("deprecation")
public class CertificateUtls {
	
	
	private static final String ISSUER_OID = "2.5.4.10";


	public static X509Certificate createTestCertificateFromProvider(QESProviders provider) throws OCSPClientException {
		
		String testCertificatesPath = null;
		try {
			testCertificatesPath = OCSPClientConfiguration.getTestCertificatesPath();
		} catch (OCSPClientConfigurationException e) {
			throw new OCSPClientException("Can not find Test Certificates Path from configuration!", e);
		}
		
		String certificateFileNameWithoutExtension = provider.getShortName().toLowerCase();
		String certificatePath = testCertificatesPath + File.separator + certificateFileNameWithoutExtension;
		
		String[] certificateExtensions = {"cer", "pem", "crt"}; 
		String certificateFullName = null;
		
		for (String certificateExtension : certificateExtensions) {
			certificateFullName = certificatePath + "." + certificateExtension;
			File certificateFile = new File(certificateFullName);
			
			if (certificateFile.exists()) {
				break;
			}
		}

		X509Certificate certificate = null;
		
		if (certificateFullName != null) {
			certificate = createX509CertificateByFilePath(certificateFullName);	
		}
		
		return certificate;
	}

	
	public static X509Certificate createX509CertificateByFilePath(String certificatePath) throws OCSPClientException {
		if (certificatePath == null) {
			throw new IllegalArgumentException("X509 Certificate path must not be NULL!");
		}
		
		CertificateFactory certificateFactory = null;
		X509Certificate certificate = null;
		FileInputStream certificateIS = null;
		
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
			certificateIS = new FileInputStream(certificatePath);

			certificate = (X509Certificate) certificateFactory.generateCertificate(certificateIS);
		
		} catch (CertificateException e) {
			throw new OCSPClientException("There is certificate exception for " + certificatePath, e);
			
		} catch (FileNotFoundException e) {
			throw new OCSPClientException("Can not be created certificate from not found certificate file " + certificatePath, e);
			
		} finally {
			if (certificateIS != null) {
				try {
					certificateIS.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return certificate;
	}


	public static String getIssuerOrganization(X509Certificate certificate) {
		X500Principal issuerPrincipal = certificate.getIssuerX500Principal();
		
		String issuerPrincipalName = issuerPrincipal.getName(X500Principal.RFC1779);
		X509Name issuerName = new X509Name(issuerPrincipalName);
		String issuerOrganization = extractElementFromX509Name(issuerName, ISSUER_OID, 0);
		
		return issuerOrganization;
	}
	
	
	private static String extractElementFromX509Name(X509Name name, String oid, int elementIndex) {
		DERObjectIdentifier derObjectIdentifier = new DERObjectIdentifier(oid);
		@SuppressWarnings("rawtypes")
		Vector valuesByIdentifier = name.getValues(derObjectIdentifier);
		
		if (valuesByIdentifier.size() > 0) {
			Object element = valuesByIdentifier.elementAt(elementIndex);
			return element.toString();
		} else {
			return "";
		}
	}
	

}
