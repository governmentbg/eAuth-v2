package bg.bulsi.egov.idp.services.temp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.eauth.model.User;
import bg.bulsi.egov.eauth.model.repository.UserRepository;
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.IdentityAttributes;
import bg.bulsi.egov.idp.dto.LevelOfAssurance;
import bg.bulsi.egov.idp.dto.LoginResponse;
import bg.bulsi.egov.idp.exception.UiBackendException;
import bg.bulsi.egov.idp.security.InvalidAuthenticationException;
import bg.bulsi.egov.idp.services.IEidProviderClient;
import bg.bulsi.egov.saml.AttributeValue;
import bg.bulsi.egov.saml.RequestedAttribute;
import bg.bulsi.egov.saml.RequestedAttributes;
import bg.bulsi.egov.saml.model.AssertionAttributes;
import bg.bulsi.egov.security.utils.PersonalIdUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	@Qualifier("idpApiClientTls")
	// @Qualifier("idpApiClient")
	private IEidProviderClient client;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private ResourceRegistryService resourceRegistry;

	@Autowired
	private ProviderService providerService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Value("${eauth.security.provider.id-secret}")
	private String encryptSecret;
	
	@Value("${nap.provider.id}")
	private String napProviderId;


	public LoginResponse login(String providerId, AuthenticationMap auth) {
		log.debug("Get Provider configuration for ID: '{}'", providerId);
		EidProviderConfig providersConfiguration = client.getIdentityProviderConfig(providerId);
		log.debug("Found provider  ID:'{}' Provider Headers: {}", providersConfiguration.getProviderId(),
				providersConfiguration.getEidProviderConnection().getCustomHeaders().toString());

		String providerName = null;
		String serviceName = null;
		try {
			AuthnRequest authnRequest = providerService.getAuthnRequest();
			providerName = (StringUtils.isNotBlank(authnRequest.getProviderName())) ? authnRequest.getProviderName()
					: providerService.getProviderOID(authnRequest);
			String serviceOID = providerService.getServiceOID(authnRequest);
			serviceName = resourceRegistry.findNameByOID(serviceOID);
			serviceName = (StringUtils.isNotBlank(serviceName)) ? serviceName : serviceOID;
		} catch (UnmarshallingException e) {
			log.error("Error: [{}]", e.getLocalizedMessage());
		}

		LoginResponse loginResponse = providerId.startsWith(napProviderId) ? buildNapLogin(providersConfiguration, auth)
				: callEidProvider(providersConfiguration, auth, serviceName, providerName);

		String userName = auth.get("username");
		
		/*
		 * Enrich from Profile if exist
		 */
		Optional<User> userProfile = enrichableUser(loginResponse);
		enrichResponseAttributes(loginResponse, userProfile);
		
		/*
		 * AuditEvent
		 */
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.principal(userName)
				.type(EventTypes.EXT_IDP_AUTHENTICATION)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		applicationEventPublisher.publishEvent(auditApplicationEvent);

		return loginResponse;
	}
	
	
	private LoginResponse callEidProvider(EidProviderConfig providerConfig, 
			AuthenticationMap authn, String serviceName, String providerName) {
		
		LoginResponse loginResponse = null;
		
		try {
			List<AssertionAttributeType> assertionTypes = loadAssertionAttributeTypes();
			InquiryResult inquiryResult = client.makeAuthInquiry(providerConfig, assertionTypes, authn, serviceName, providerName);
			OffsetDateTime validity = inquiryResult.getValidity();
			String relyingPartyRequestID = inquiryResult.getRelyingPartyRequestID();
			log.info("inquiryResult: [{}]", relyingPartyRequestID);
			loginResponse = client.getAuthInquiryResponse(providerConfig, relyingPartyRequestID, validity);
		} catch (UiBackendException e) {
			int errCode = e.getResponceCode();
			if (errCode == 204 || (errCode >= 400 && errCode <= 499)) {
				log.error(e.getMessage(), e);
				throw new InvalidAuthenticationException(e.getMessage() + ": [" + errCode + "]");
			} else if (errCode >= 500 && errCode <= 599) {
				log.error(e.getMessage(), e);
				throw new AuthenticationServiceException(e.getMessage() + ": [" + errCode + "]");
			} else {
				log.error(e.getMessage(), e);
				throw new UiBackendException(e.getMessage(), e, errCode);
			}
		} catch (IllegalStateException e) {
			log.error(e.getMessage(), e);
			throw new InvalidAuthenticationException("ResponceTimeout: " + e.getMessage());
		}
		
		return loginResponse;
	}

	private LoginResponse buildNapLogin(EidProviderConfig providerConfig, AuthenticationMap authn) {
		List<IdentityAttributes> attributes = new ArrayList<>();
		String defaultName = authn.get("defaultName");
		String identifier = authn.get("egn");
		// String typeCode = auth.get("typeCode");
		// String jwt = auth.get("jwt");
		loadRequired(attributes, defaultName, identifier);

		return new LoginResponse()
				.providerId(providerConfig.getProviderId())
				.loa(LevelOfAssurance.fromValue(providerConfig.getLoa().name()))
				.attributes(attributes);
	}
	

	public List<IdentityAttributes> loadAllAttributes(Map<String, String> certificateDn, List<String> certificatesX509) {
		List<IdentityAttributes> attributes = new ArrayList<>();
		loadRequired(attributes, certificateDn.get("CN"), certificateDn.get("serialNumber"));
		loadAdditional(attributes, certificateDn);
		if (certificatesX509 != null && !certificatesX509.isEmpty()) {
			loadCertificateX509(attributes, certificatesX509.get(0));
		}
		return attributes;
	}


	private void addAttribute(List<IdentityAttributes> attributes, AssertionAttributes key, String value) {
		for (AssertionAttributes enumItem : AssertionAttributes.values()) {
			if (enumItem.equals(key)) {
				IdentityAttributes identityAttribute = new IdentityAttributes();
				identityAttribute.setOid(null);
				identityAttribute.setUrn(enumItem.getEidUrn());
				identityAttribute.setValue(value);
				attributes.add(identityAttribute);
			}
		}
	}

	/**
	 * Only required attributes are name and identificationNumber
	 * @param attributes
	 * @param name
	 * @param identifier
	 */
	private void loadRequired(List<IdentityAttributes> attributes, String name, String identifier) {
		/*
		 * 1) PNO for identification based on (national) personal number (national civic
		 * registration number) 2) 2 character ISO country code; 3) hyphen-minus
		 * "-"(0x2D (ASCII), U+002D (UTF-8)); and 4) identifier (according to country
		 * and identity type reference).
		 */
		if (!identifier.contains("-")) {
			identifier = "PNOBG-" + identifier;
		}
		
		addAttribute(attributes, AssertionAttributes.PERSON_NAME, name);
		addAttribute(attributes, AssertionAttributes.PERSON_IDENTIFIER, identifier);
	}

	private void loadAdditional(List<IdentityAttributes> attributes, Map<String, String> certificateDn) {

		String email = certificateDn.get("emailAddress");
		
		List<RequestedAttribute> requestedAttr = getRequestedAttributes();

		String identifier = certificateDn.get("serialNumber");
		identifier = identifier.substring(identifier.indexOf('-') + 1); // strip identifier string
		String ecryptedIdentifier = PersonalIdUtils.encrypt(identifier, encryptSecret);
		log.debug("ecryptedIdentifier: [{}]", ecryptedIdentifier);
		Optional<User> userEntity = userRepository.findByPersonID(ecryptedIdentifier);
		
		log.debug("attributes size: [{}]", requestedAttr.size());
		for (RequestedAttribute attr : requestedAttr) {
			log.debug("name: [{}]", attr.getName());
			for (AttributeValue attrValue: attr.getAttributeValues()) {
				log.debug("attrValue: [{}]", attrValue.getValue());
				// TODO: fix that: maybe if attr.isRequired() ?
				String urn = attrValue.getValue();
				if (AssertionAttributes.EMAIL.getEidUrn().equals(urn) 
						&& StringUtils.isNotBlank(email)) {
					addAttribute(attributes, AssertionAttributes.EMAIL, email);
				} else if (userEntity.isPresent()) {
					loadProfileAttributes(attributes, urn, userEntity.get());
				}
			}
		}

	}
	
	private void loadCertificateX509(List<IdentityAttributes> attributes, String certX509) {
		addAttribute(attributes, AssertionAttributes.CERTIFICATE, certX509);
	}
	
	private void loadProfileAttributes(List<IdentityAttributes> attributes, String urn, User user) {
		if (AssertionAttributeType.LATINNAME.toString().equals(urn)) {
			addAttribute(attributes, AssertionAttributes.LATIN_NAME, user.getName());
		} else if (AssertionAttributeType.PHONE.toString().equals(urn)) {
			addAttribute(attributes, AssertionAttributes.PHONE, user.getPhoneNumber());
		} else if (AssertionAttributeType.PLACEOFBIRTH.toString().equals(urn) 
			&& user.getAddress() != null && user.getAddress().getEkatte() != null) {
			// TODO FORMAT
			addAttribute(attributes, AssertionAttributes.PLACE_OF_BIRTH, user.getAddress().getEkatte().getPlace());
		} else if (AssertionAttributeType.CANONICALRESIDENCEADDRESS.toString().equals(urn)
			&& user.getAddress() != null) {
			// TODO FORMAT
			addAttribute(attributes, AssertionAttributes.CANNONICAL_RESIDENCE_ADDRESS, user.getAddress().getAddressDescription());
		}
	}
	
	private List<RequestedAttribute> getRequestedAttributes() {
		List<RequestedAttribute> requestedAttr = new ArrayList<>();
		try {
			AuthnRequest authnRequest = providerService.getAuthnRequest();
			
			List<XMLObject> unknownXMLObjects = authnRequest.getExtensions().getUnknownXMLObjects();
			log.debug("unknownXMLObjects size: [{}]", unknownXMLObjects.size());
			
			// ако има допълнителни изискуеми атрибути в заявката, които да се върнат в атестата
			if (unknownXMLObjects.size() > 1 ) {
				RequestedAttributes requestedAttributes = (RequestedAttributes) unknownXMLObjects.get(1);
				requestedAttr.addAll(requestedAttributes.getRequestedAttributes());
			}
		} catch (UnmarshallingException e) {
			log.error("error: {}", e);
		}
		
		return requestedAttr;
	}
	
	private List<AssertionAttributeType> loadAssertionAttributeTypes() {
		List<AssertionAttributeType> assertionAttrTypes = new ArrayList<>();
		
		List<RequestedAttribute> requestedAttributes = getRequestedAttributes();
		
		requestedAttributes.stream().filter(attr-> !attr.getName().contains("canonical")).forEach(attr ->  assertionAttrTypes.add(AssertionAttributeType.fromValue(attr.getName())));
		return assertionAttrTypes;
	}
	
	private Optional<User> enrichableUser(LoginResponse loginResponse) {
		
		List<IdentityAttributes> loginResponseAttributes = loginResponse.getAttributes();
		Optional<User> userEntity = Optional.empty();
		
		/*
personID		PERSONIDENTIFIER("urn:egov:bg:eauth:2.0:attributes:personIdentifier"),
name		    PERSONNAME("urn:egov:bg:eauth:2.0:attributes:personName"),
email		    EMAIL("urn:egov:bg:eauth:2.0:attributes:email"),
phoneNumber	    PHONE("urn:egov:bg:eauth:2.0:attributes:phone"),
name		    LATINNAME("urn:egov:bg:eauth:2.0:attributes:latinName"),
			    BIRTHNAME("urn:egov:bg:eauth:2.0:attributes:birthName"),
			    DATEOFBIRTH("urn:egov:bg:eauth:2.0:attributes:dateOfBirth"),
			    GENDER("urn:egov:bg:eauth:2.0:attributes:gender"),
			    PLACEOFBIRTH("urn:egov:bg:eauth:2.0:attributes:placeOfBirth"),
address		    CANONICALRESIDENCEADDRESS("urn:egov:bg:eauth:2.0:attributes:canonicalResidenceAddress");
		 */
		List<AssertionAttributeType> updatetableAttributes = Arrays.asList(
						AssertionAttributeType.PERSONNAME,
						AssertionAttributeType.LATINNAME,
						AssertionAttributeType.EMAIL,
						AssertionAttributeType.PHONE,
						AssertionAttributeType.PLACEOFBIRTH,
						AssertionAttributeType.CANONICALRESIDENCEADDRESS
			);
		
		
		Optional<Boolean> isCheckProfile = loginResponseAttributes.stream()
			.filter(att -> StringUtils.isBlank(att.getValue()))
			.map(att -> att.getUrn())
			.filter(urn -> updatetableAttributes.stream()
									.map(type -> type.toString())
									.anyMatch(updUrn -> urn.equals(updUrn))
									)
			.map(r -> true)
			.findFirst();


		if (isCheckProfile.isPresent()) {			
			
			Optional<IdentityAttributes> identityAttributePersonId = loginResponseAttributes.stream()
			.filter(identAtt -> AssertionAttributeType.PERSONIDENTIFIER.toString().equals(identAtt.getUrn()))
			.findFirst();
			
			String identifier = null;
			if (identityAttributePersonId.isPresent()) {
				identifier = identityAttributePersonId.get().getValue();
				identifier = identifier.substring(identifier.indexOf('-') + 1); // strip identifier string
				String ecryptedIdentifier = PersonalIdUtils.encrypt(identifier, encryptSecret);
				log.debug("ecryptedIdentifier: [{}]", ecryptedIdentifier);
				userEntity = userRepository.findByPersonID(ecryptedIdentifier);
			} else {
				log.warn("identity attribute person Id is not present!");
			}
			
		} 
		
		return userEntity;
	} 
	

	private void enrichResponseAttributes(LoginResponse loginResponse, Optional<User> userProfile) {
		if (userProfile.isPresent()) {
			User user = userProfile.get();
			
			loginResponse.getAttributes().stream()
				.filter(att -> StringUtils.isBlank(att.getValue()))
					.forEach(att -> {
						String urn = att.getUrn();
						if (AssertionAttributeType.PERSONNAME.toString().equals(urn)) {
							att.setValue(user.getName());
						} else if (AssertionAttributeType.LATINNAME.toString().equals(urn)) {
							att.setValue(user.getName());
						} else if (AssertionAttributeType.EMAIL.toString().equals(urn)) {
							att.setValue(user.getEmail());
						} else if (AssertionAttributeType.PHONE.toString().equals(urn)) {
							att.setValue(user.getPhoneNumber());
						} else if (AssertionAttributeType.PLACEOFBIRTH.toString().equals(urn)) {
							// TODO FORMAT
							if (user.getAddress() != null && user.getAddress().getEkatte() != null) {
								att.setValue(user.getAddress().getEkatte().getPlace());
							}
						} else if (AssertionAttributeType.CANONICALRESIDENCEADDRESS.toString().equals(urn)) {
							// TODO FORMAT
							if (user.getAddress() != null) {
								att.setValue(user.getAddress().getAddressDescription());
							}
						}
					});					
		}
	}
	
}
