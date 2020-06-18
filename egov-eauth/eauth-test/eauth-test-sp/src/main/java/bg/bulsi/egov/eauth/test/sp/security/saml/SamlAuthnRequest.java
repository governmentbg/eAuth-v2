package bg.bulsi.egov.eauth.test.sp.security.saml;

import static org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport.getBuilderFactory;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Clock;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.springframework.security.saml2.Saml2Exception;
import org.springframework.security.saml2.credentials.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationRequestFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import bg.bulsi.egov.saml.LevelOfAssurance;
import bg.bulsi.egov.saml.Provider;
import bg.bulsi.egov.saml.RequestedService;
import bg.bulsi.egov.saml.Service;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

@Deprecated
@Slf4j
// @Component
public class SamlAuthnRequest implements Saml2AuthenticationRequestFactory {

	private Clock clock = Clock.systemUTC();
	private String protocolBinding = SAMLConstants.SAML2_POST_BINDING_URI;

	@Override
	public String createAuthenticationRequest(Saml2AuthenticationRequest request) {

		printRequest(request);

		AuthnRequest auth = buildSAMLObject(AuthnRequest.class);
		auth.setID("ARQ" + UUID.randomUUID().toString().substring(1));
		auth.setIssueInstant(new DateTime(this.clock.millis()));
		auth.setForceAuthn(Boolean.TRUE);
		auth.setIsPassive(Boolean.FALSE);
		auth.setProtocolBinding(protocolBinding);
		Issuer issuer = buildSAMLObject(Issuer.class);
		issuer.setValue(request.getIssuer());
		auth.setIssuer(issuer);
		auth.setDestination(request.getDestination());
		auth.setAssertionConsumerServiceURL(request.getAssertionConsumerServiceUrl());
		
		Extensions extensions = buildSAMLObject(Extensions .class);
	    auth.setExtensions(extensions);

	    RequestedService requestedService = buildSAMLObject(RequestedService.class);

	    Service service = buildSAMLObject(Service.class);
	    service.setValue("123");
	    Provider provider = buildSAMLObject(Provider.class);
	    provider.setValue("456");
	    LevelOfAssurance loa = buildSAMLObject(LevelOfAssurance.class);
	    loa.setValue("LOW");
	    
	    requestedService.setService(service);
	    requestedService.setProvider(provider);
	    requestedService.setLevelOfAssurance(loa);

	    extensions.getUnknownXMLObjects().add(requestedService);

		return toXml(auth, request.getCredentials(), request.getIssuer());
	}

	private void printRequest(Saml2AuthenticationRequest request) {
		// SSO endpoint
		String dest = request.getDestination();
		log.info("dest: [{}]", dest);
		String url = request.getAssertionConsumerServiceUrl();
		log.info("url: [{}]", url);
		String iss = request.getIssuer();
		log.info("issuer: [{}]", iss);
		List<Saml2X509Credential> creds = request.getCredentials();
		for (Saml2X509Credential cr : creds) {
			X509Certificate cert = cr.getCertificate();
			log.info("type: [{}]", cert.getType());
			log.info("ver: [{}]", cert.getVersion());
			PrivateKey privateKey = cr.getPrivateKey();
			log.info("alg: [{}]", privateKey.getAlgorithm());
			log.info("format: [{}]", privateKey.getFormat());
		}

	}

	private String toXml(XMLObject object, List<Saml2X509Credential> signingCredentials, String localSpEntityId) {
		if (object instanceof SignableSAMLObject && null != hasSigningCredential(signingCredentials)) {
			signXmlObject((SignableSAMLObject) object, signingCredentials, localSpEntityId);
		}
		final MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
		try {
			Element element = marshallerFactory.getMarshaller(object).marshall(object);
			return SerializeSupport.nodeToString(element);
		} catch (MarshallingException e) {
			throw new Saml2Exception(e);
		}
	}

	private void signXmlObject(SignableSAMLObject object, List<Saml2X509Credential> signingCredentials,
			String entityId) {
		SignatureSigningParameters parameters = new SignatureSigningParameters();
		Credential credential = getSigningCredential(signingCredentials, entityId);
		parameters.setSigningCredential(credential);
		parameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		parameters.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
		parameters.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
		try {
			SignatureSupport.signObject(object, parameters);
		} catch (MarshallingException | SignatureException | SecurityException e) {
			throw new Saml2Exception(e);
		}

	}

	private Credential getSigningCredential(List<Saml2X509Credential> signingCredential, String localSpEntityId) {
		Saml2X509Credential credential = hasSigningCredential(signingCredential);
		if (credential == null) {
			throw new Saml2Exception("no signing credential configured");
		}
		BasicCredential cred = getBasicCredential(credential);
		cred.setEntityId(localSpEntityId);
		cred.setUsageType(UsageType.SIGNING);
		return cred;
	}

	private Saml2X509Credential hasSigningCredential(List<Saml2X509Credential> credentials) {
		for (Saml2X509Credential c : credentials) {
			if (c.isSigningCredential()) {
				return c;
			}
		}
		return null;
	}

	private BasicX509Credential getBasicCredential(Saml2X509Credential credential) {
		return CredentialSupport.getSimpleCredential(credential.getCertificate(), credential.getPrivateKey());
	}

	private <T> T buildSAMLObject(final Class<T> clazz) {
		try {
			QName defaultElementName = (QName) clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
			return (T) getBuilderFactory().getBuilder(defaultElementName).buildObject(defaultElementName);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new Saml2Exception("Could not create SAML object", e);
		}
	}
}
