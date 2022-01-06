package org.springframework.security.saml2.provider.service.authentication;

import static bg.bulsi.egov.eauth.saml.Saml2Utils.samlEncode;
import static bg.bulsi.egov.eauth.saml.Saml2Utils.samlDeflate;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.Issuer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.saml2.credentials.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.Saml2RedirectAuthenticationRequest.Builder;

import bg.bulsi.egov.eauth.saml.OpenSamlImplementation;
import bg.bulsi.egov.saml.AttributeValue;
import bg.bulsi.egov.saml.LevelOfAssurance;
import bg.bulsi.egov.saml.Provider;
import bg.bulsi.egov.saml.RequestedAttribute;
import bg.bulsi.egov.saml.RequestedAttributes;
import bg.bulsi.egov.saml.RequestedService;
import bg.bulsi.egov.saml.Service;
import bg.bulsi.egov.saml.model.AssertionAttributes;
import bg.bulsi.egov.saml.schema.ClaimsTypeEnum;

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
	public Saml2RedirectAuthenticationRequest createRedirectAuthenticationRequest(Saml2AuthenticationRequestContext context) {
		AuthnRequest auth = this.saml.buildSamlObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
		auth.setID("ARQ" + UUID.randomUUID().toString().substring(1));
		auth.setIssueInstant(new DateTime(this.clock.millis()));
		auth.setForceAuthn(Boolean.FALSE); // TODO: how to log out of SP?
		auth.setIsPassive(Boolean.FALSE);
		auth.setProtocolBinding(protocolBinding);
		Issuer issuer = this.saml.buildSamlObject(Issuer.DEFAULT_ELEMENT_NAME);
		issuer.setValue(context.getIssuer());
		auth.setIssuer(issuer);
		auth.setDestination(context.getDestination());
		auth.setAssertionConsumerServiceURL(context.getAssertionConsumerServiceUrl());
		
		Extensions extensions = this.saml.buildSamlObject(Extensions.DEFAULT_ELEMENT_NAME);
		auth.setExtensions(extensions);

		RequestedService requestedService = this.saml.buildSamlObject(RequestedService.DEFAULT_ELEMENT_NAME);

		Service service = this.saml.buildSamlObject(Service.DEFAULT_ELEMENT_NAME);
		service.setValue(serviceOID);
		Provider provider = this.saml.buildSamlObject(Provider.DEFAULT_ELEMENT_NAME);
		provider.setValue(providerOID);
		LevelOfAssurance loa = this.saml.buildSamlObject(LevelOfAssurance.DEFAULT_ELEMENT_NAME);
		loa.setValue(levelOfAssurance);

		requestedService.setService(service);
		requestedService.setProvider(provider);
		requestedService.setLevelOfAssurance(loa);
		extensions.getUnknownXMLObjects().add(requestedService);
		
		RequestedAttributes requestedAttributes = this.saml.buildSamlObject(RequestedAttributes.DEFAULT_ELEMENT_NAME);
		
		for (ClaimsTypeEnum claim : ClaimsTypeEnum.values()) {
			if (!claim.equals(ClaimsTypeEnum.URN_EGOV_BG_EAUTH_2_0_ATTRIBUTES_PERSON_IDENTIFIER) 
					&& !claim.equals(ClaimsTypeEnum.URN_EGOV_BG_EAUTH_2_0_ATTRIBUTES_PERSON_NAME)
							&& !claim.value().contains(":1.0:")) {
				
				RequestedAttribute requestedAttribute = this.saml.buildSamlObject(RequestedAttribute.DEFAULT_ELEMENT_NAME);
				AssertionAttributes assertionAttributes = AssertionAttributes.fromUrn(claim.value());
				requestedAttribute.setName(assertionAttributes.getEidUrn());
				requestedAttribute.setFriendlyName(assertionAttributes.getFriendlyName());
				requestedAttribute.setRequired(assertionAttributes.isRequired());
				requestedAttribute.setNameFormat(assertionAttributes.getNameFormat());
				
				AttributeValue attributeValue = this.saml.buildSamlObject(AttributeValue.DEFAULT_ELEMENT_NAME);
				attributeValue.setValue(claim.value());
				
				requestedAttribute.getAttributeValues().add(attributeValue);
				
				requestedAttributes.getRequestedAttributes().add(requestedAttribute);
			}
		} 
		
		extensions.getUnknownXMLObjects().add(requestedAttributes);

		String xml = this.saml.serialize(auth);
		Builder result = Saml2RedirectAuthenticationRequest.withAuthenticationRequestContext(context);
		String deflatedAndEncoded = samlEncode(samlDeflate(xml));
		result.samlRequest(deflatedAndEncoded)
				.relayState(context.getRelayState());

		if (context.getRelyingPartyRegistration().getProviderDetails().isSignAuthNRequest()) {
			List<Saml2X509Credential> signingCredentials = context.getRelyingPartyRegistration().getSigningCredentials();
			Map<String, String> signedParams = this.saml.signQueryParameters(
					signingCredentials,
					deflatedAndEncoded,
					context.getRelayState()
			);
			result.samlRequest(signedParams.get("SAMLRequest"))
					.relayState(signedParams.get("RelayState"))
					.sigAlg(signedParams.get("SigAlg"))
					.signature(signedParams.get("Signature"));
		}

		return result.build();
	}
	
	
}
