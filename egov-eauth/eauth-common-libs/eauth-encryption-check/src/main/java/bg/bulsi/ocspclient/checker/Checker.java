package bg.bulsi.ocspclient.checker;

import bg.bulsi.ocspclient.common.Base64Utility;
import bg.bulsi.ocspclient.common.OcspClientUtils;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.ocsp.*;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;


public class Checker {
	
	
	private CertificateID generateCertificateID(X509Certificate certificate, X509Certificate caCertificate, Provider provider) throws OCSPException {
		CertificateID certificateID = null;

		try {
			BigInteger certificateSerialNumber = certificate.getSerialNumber();
			certificateID = new CertificateID(CertificateID.HASH_SHA1, caCertificate, certificateSerialNumber);

		} catch (OCSPException e) {
			e.printStackTrace();
			throw new OCSPException(
					"Can not generate a valid certificate ID. CA certificate encoding is not valid", e);
		}

		return certificateID;
	}

	
	private OCSPResp sendOCSPRequest(String ocspURL, CertificateID certificateID, X509Certificate certificate)
			throws OCSPException {
		try {
			OCSPReqGenerator ocspReqGenerator = new OCSPReqGenerator();
			ocspReqGenerator.addRequest(certificateID);

			X509Principal subjectX509Principal = PrincipalUtil.getSubjectX509Principal(certificate);
			GeneralName name = new GeneralName(subjectX509Principal);
			ocspReqGenerator.setRequestorName(name);
			
			OCSPReq ocspReq = ocspReqGenerator.generate();
			OCSPResp OCSPResponse = sendOCSPRequest(ocspReq, ocspURL);
			
			return OCSPResponse;
		} catch (CertificateEncodingException cee) {
			throw new OCSPException("Can not send OCSP request. Certificate encoding is not valid", cee);
		} catch (OCSPException oe) {
			throw new OCSPException("Can not generate OCSP request", oe);
		}
	}

	
	private OCSPResp sendOCSPRequest(OCSPReq ocspReq, String ocspURL) throws OCSPException {
		OCSPResp ocspResponse = null;

		try {
			byte[] requestInBytes = ocspReq.getEncoded();
			URL url = new URL(ocspURL);
			
			URLConnection con = url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(10000);
			con.setAllowUserInteraction(false);
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestProperty("Content-Type", "application/ocsp-request");
			con.setRequestProperty("Accept", "application/ocsp-response");
			
			OutputStream os = con.getOutputStream();
			os.write(requestInBytes);
			os.flush();
			os.close();

			// Read the response
			byte[] responseInBytes = OcspClientUtils.inputStreamToByteArray(con.getInputStream());

			ocspResponse = new OCSPResp(responseInBytes);
		} catch (IOException ioe) {
			throw new OCSPException("Can not recover response from server " + ocspURL, ioe);
		}

		return ocspResponse;
	}
	

	@SuppressWarnings("unused")
	private OCSPResp sendOCSPRequestPOST(String postURL, X509Certificate certificate) throws OCSPException {
		OCSPResp ocspResp = null;

		try {
			byte[] certificateInBytes = certificate.getEncoded();
			String certBase64 = Base64Utility.encode(certificateInBytes);
			
			URL url = new URL(postURL);
			URLConnection con = url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(10000);
			con.setAllowUserInteraction(false);
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestProperty("Content-Type", "application/octet-stream");
			OutputStream os = con.getOutputStream();
			os.write(certBase64.getBytes());
			os.close();

			byte[] resp64 = OcspClientUtils.inputStreamToByteArray(con.getInputStream());
			byte[] bresp = Base64Utility.decode(resp64);

			OCSPResponse resp = new OCSPResponse(null);
			if (bresp == null) {
				ocspResp = new OCSPResp(resp);
			} else {
				if (bresp.length == 0) {
					ocspResp = new OCSPResp(resp);
				} else {
					ocspResp = new OCSPResp(bresp);
				}
			}
		} catch (CertificateEncodingException cee) {
			throw new OCSPException("Can not send OCSP request. Certificate encoding is not valid", cee);
		} catch (IOException ioe) {
			throw new OCSPException("Can not recover response from server " + postURL, ioe);
		}

		return ocspResp;
	}
	
	
	public OCSPResponseDetails getCertificateStatus(X509Certificate certificate, X509Certificate caCertificate, String ocspURL) {
		OCSPResponseDetails ocspResponseDetails = getCertificateStatus(certificate, caCertificate, null, ocspURL);
		return ocspResponseDetails;
	}

	
	@SuppressWarnings("deprecation")
	public OCSPResponseDetails getCertificateStatus(X509Certificate certificate, X509Certificate caCertificate, X509Certificate ocspCertificate, String ocspURL) {

		Provider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		
		OCSPResponseDetails responseDetails = new OCSPResponseDetails();

		CertificateID certificateID = null;

		try {
			certificateID = generateCertificateID(certificate, caCertificate, provider);
		} catch (OCSPException ccoe) {
			responseDetails.setValid(false);
			responseDetails.addError(ccoe.getMessage());
			return responseDetails;
		}

		OCSPResp ocspResponse = null;
		try {
			ocspResponse = sendOCSPRequest(ocspURL, certificateID, certificate);
		} catch (OCSPException e) {
			e.printStackTrace();
			
			responseDetails.setValid(false);
			responseDetails.addError(e.getMessage());
			return responseDetails;
		}
		
		if (ocspResponse == null) {
			responseDetails.setValid(false);
			responseDetails.addError("An internal error occured in the OCSP Server!");
			return responseDetails;
		}

		try {
			byte[] ocspResponseInBytes = ocspResponse.getEncoded();
			responseDetails.setResponseData(ocspResponseInBytes);
		} catch (IOException ioe) {
			responseDetails.setValid(false);
			responseDetails.addError("Can not get encoded content from OCSP respose");
			return responseDetails;
		}

		int status = ocspResponse.getStatus();
		if (status != OCSPRespStatus.SUCCESSFUL) {
			responseDetails.setValid(false);

			switch (status) {
			case OCSPRespStatus.INTERNAL_ERROR:
				responseDetails.addError("An internal error occured in the OCSP Server");
				break;
			case OCSPRespStatus.MALFORMED_REQUEST:
				responseDetails.addError("Your request did not fit the RFC 2560 syntax");
				break;
			case OCSPRespStatus.SIGREQUIRED:
				responseDetails.addError("Your request was not signed");
				break;
			case OCSPRespStatus.TRY_LATER:
				responseDetails.addError("The server was too busy to answer you");
				break;
			case OCSPRespStatus.UNAUTHORIZED:
				responseDetails.addError("The server could not authenticate you");
				break;
			default:
				responseDetails.addError("Unknown OCSPResponse status code " + status);
			}

			return responseDetails;
		}

		// Read the info from the response
		BasicOCSPResp basicOCSPResp = null;

		try {
			basicOCSPResp = (BasicOCSPResp) ocspResponse.getResponseObject();
		} catch (OCSPException oe) {
			responseDetails.setValid(false);
			responseDetails.addError("Can not retrieve basic reponse object from server response");
			return responseDetails;
		}

		/*
		 * Check the validity of OCSP response signed by the provider.
		 */
		if (ocspCertificate != null) {
			try {
				PublicKey publicKey = ocspCertificate.getPublicKey();
				String name = provider.getName();
				
				boolean isVerified = basicOCSPResp.verify(publicKey, name);
				
				if (!isVerified) {
					responseDetails.setValid(false);
					responseDetails.addError("OCSP Signature verification error");

					return responseDetails;
				}
			} catch (NoSuchProviderException nspe) {
				responseDetails.setValid(false);
				responseDetails.addError("OCSP response verification error. Provider not available");
				return responseDetails;
				
			} catch (OCSPException oe) {
				responseDetails.setValid(false);
				responseDetails.addError("Can not verify OCSP response");
				return responseDetails;
			}
		}

		SingleResp[] ocspResponses = basicOCSPResp.getResponseData().getResponses();

		boolean isValidationOk = false;
		for (int i = 0; i < ocspResponses.length; i++) {
			
			SingleResp singleOcspResponse = ocspResponses[i];
			CertificateID certIdOcspResponse = singleOcspResponse.getCertID();
			
			if (certIdOcspResponse != null) {
				boolean isCertidicateAlgorithmTheSame = certificateID.getHashAlgOID().equals(certIdOcspResponse.getHashAlgOID());
				boolean isCertificateSerialNumberTheSame = certificateID.getSerialNumber().equals(certIdOcspResponse.getSerialNumber());
				boolean isIssuerKeyNameTheSame = compareDigests(certificateID.getIssuerNameHash(), certIdOcspResponse.getIssuerNameHash());
				boolean isIssuerKeyHashTheSame = compareDigests(certificateID.getIssuerKeyHash(), certIdOcspResponse.getIssuerKeyHash());

				if (isCertidicateAlgorithmTheSame && isCertificateSerialNumberTheSame && isIssuerKeyNameTheSame && isIssuerKeyHashTheSame) {
					isValidationOk = true;
					
					Object certStatus = singleOcspResponse.getCertStatus();
					
					if (certStatus == CertificateStatus.GOOD) {
					
					} else if (certStatus instanceof RevokedStatus) {
						responseDetails.setValid(false);
						responseDetails.addError("Certificate status is REVOKED!");
						return responseDetails;
					
					} else if (certStatus instanceof UnknownStatus) {
						responseDetails.setValid(false);
						responseDetails.addError("Certificate status is UNKNOWN!");
						return responseDetails;
					}

					break;
				}
			}
		}

		if (!isValidationOk) {
			responseDetails.setValid(false);
			responseDetails.addError("Bad OCSP response status");
			return responseDetails;
		}

		responseDetails.setValid(true);
		return responseDetails;
	}

	
	private boolean compareDigests(byte[] digest1, byte[] digest2) {
		boolean areEqual = (digest1 != null) && (digest2 != null) && (digest1.length == digest2.length);

		for (int i = 0; areEqual && (i < digest1.length); i++) {
			if (digest1[i] != digest2[i]) {
				areEqual = false;
			}
		}

		return areEqual;
	}

}
