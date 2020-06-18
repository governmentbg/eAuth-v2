package org.springframework.security.saml2.provider.service.authentication;

import bg.bulsi.egov.saml.LevelOfAssurance;
import bg.bulsi.egov.saml.Provider;
import bg.bulsi.egov.saml.RequestedService;
import bg.bulsi.egov.saml.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.UUID;
import org.joda.time.DateTime;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.Issuer;
import org.springframework.beans.factory.annotation.Value;

import bg.bulsi.egov.eauth.test.sp.saml.OpenSamlImplementation;

@Slf4j
public class AuthenticationRequestFactory extends OpenSamlAuthenticationRequestFactory {

	private Clock clock = Clock.systemUTC();
	private final OpenSamlImplementation saml = OpenSamlImplementation.getInstance();
	private String protocolBinding = SAMLConstants.SAML2_POST_BINDING_URI;

	@Value("${sp.service.oid:2.16.100.1.1.1.1.13.1.1.2}")
	private String serviceOID;

	@Value("${sp.provider.oid:2.16.100.1.1.1.1.13}")
	private String providerOID;

	@Value("${sp.loa:LOW}")
	private String levelOfAssurance;

	public AuthenticationRequestFactory() {

	}

	@Override
	public String createAuthenticationRequest(Saml2AuthenticationRequest request) {
		AuthnRequest auth = this.saml.buildSAMLObject(AuthnRequest.class);
		auth.setID("ARQ" + UUID.randomUUID().toString().substring(1));
		auth.setIssueInstant(new DateTime(this.clock.millis()));
		auth.setForceAuthn(Boolean.FALSE); // TODO: how to log out of SP?
		auth.setIsPassive(Boolean.FALSE);
		auth.setProtocolBinding(protocolBinding);
		Issuer issuer = this.saml.buildSAMLObject(Issuer.class);
		issuer.setValue(request.getIssuer());
		auth.setIssuer(issuer);
		auth.setDestination(request.getDestination());
		auth.setAssertionConsumerServiceURL(request.getAssertionConsumerServiceUrl());

		Extensions extensions = this.saml.buildSAMLObject(Extensions.class);
		auth.setExtensions(extensions);

		RequestedService requestedService = this.saml.buildSAMLObject(RequestedService.class);

		Service service = this.saml.buildSAMLObject(Service.class);
		service.setValue(serviceOID);
		Provider provider = this.saml.buildSAMLObject(Provider.class);
		provider.setValue(providerOID);
		LevelOfAssurance loa = this.saml.buildSAMLObject(LevelOfAssurance.class);
		loa.setValue(levelOfAssurance);

		requestedService.setService(service);
		requestedService.setProvider(provider);
		requestedService.setLevelOfAssurance(loa);

		extensions.getUnknownXMLObjects().add(requestedService);

		return this.saml.toXml(auth, request.getCredentials(), request.getIssuer());
	}
}
