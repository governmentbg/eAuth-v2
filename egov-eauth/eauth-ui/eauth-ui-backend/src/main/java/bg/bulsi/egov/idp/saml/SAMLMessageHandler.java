package bg.bulsi.egov.idp.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.joda.time.DateTime;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.servlet.HttpServletRequestMessageDecoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.messaging.handler.impl.BasicMessageHandlerChain;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.common.binding.impl.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.binding.security.impl.MessageLifetimeSecurityHandler;
import org.opensaml.saml.common.binding.security.impl.ReceivedEndpointSecurityHandler;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPPostDecoder;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPRedirectDeflateDecoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.common.xml.MetadataBuilderFactoryUtil;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.saml.SAMLBuilder;
import bg.bulsi.egov.eauth.saml.SAMLPrincipal;
import bg.bulsi.egov.eauth.saml.keystore.KeyManager;
import bg.bulsi.egov.idp.services.temp.ProviderService;
import bg.bulsi.egov.idp.services.validator.AuthnRequestValidator;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

//import bg.bulsi.egov.saml.ProxiedSAMLContextProviderLB;

@Slf4j
@Component
public class SAMLMessageHandler {

	private KeyManager keyManager;
	// private final SAMLMessageEncoder encoder;
	private IdpConfigurationProperties idpConfiguration;
	// private final Collection<SAMLMessageDecoder> decoders;
	// private final SecurityPolicyResolver resolver;
	// private final List<ValidatorSuite> validatorSuites;
	// private final ProxiedSAMLContextProviderLB proxiedSAMLContextProviderLB;
	private ApplicationEventPublisher applicationEventPublisher;
	private AuthnRequestValidator authnValidator;
	private ProviderService providerService;
	private Environment environment;

//	 @Value("${validate.authn.oids}")
	private boolean validateOidsEnabled = false;

//	 @Value("${validate.authn.claims}")
	private boolean validateClaimsEnabled = true;
	
	private RestTemplate restTemplate;

	@Autowired
	public SAMLMessageHandler(
			KeyManager keyManager,
//			Collection<SAMLMessageDecoder> decoders,
//			SAMLMessageEncoder encoder,
//			SecurityPolicyResolver securityPolicyResolver,
			IdpConfigurationProperties idpConfiguration,
			ApplicationEventPublisher applicationEventPublisher,
			AuthnRequestValidator authnValidator,
			ProviderService providerService,
			RestTemplate restTemplate,
			Environment environment
			) {

		 this.keyManager = keyManager;
		// this.encoder = encoder;
		// this.decoders = decoders;
		// this.resolver = securityPolicyResolver;
		 this.idpConfiguration = idpConfiguration;
		/*
		 * this.validatorSuites = asList(
		 * getValidatorSuite("saml2-core-schema-validator"),
		 * getValidatorSuite("saml2-core-spec-validator")
		 * );
		 */
		//this.saml = OpenSamlImplementation.getInstance();
		/*
		 * Context provider which overrides request attributes with values of the load-balancer or reverse-proxy in front
		 * of the local application. The settings help to provide correct redirect URls and verify destination URLs during
		 * SAML processing.
		 */
		// this.proxiedSAMLContextProviderLB = new ProxiedSAMLContextProviderLB(new URI(idpBaseUrl));
		this.applicationEventPublisher = applicationEventPublisher;
		this.authnValidator = authnValidator;
		this.providerService = providerService;
		this.restTemplate = restTemplate;
		this.environment = environment;
	}


	@SuppressWarnings("unchecked")
	public MessageContext<SAMLObject> extractSAMLMessageContext(HttpServletRequest request, HttpServletResponse response, boolean isPostRequest)
			throws MessageDecodingException, ComponentInitializationException, MessageHandlerException, SignatureException, ParserConfigurationException, SAXException, IOException, UnmarshallingException, CertificateException {

		HttpServletRequestMessageDecoder<SAMLObject> decoder = (HttpServletRequestMessageDecoder<SAMLObject>) samlMessageDecoder(
				isPostRequest);

		try {

			decoder.setHttpServletRequest(request);
			decoder.initialize();
			decoder.decode();

			MessageContext<SAMLObject> context = decoder.getMessageContext();

			AuthnRequest authnRequest = (AuthnRequest) context.getMessage();

			// TODO - тук трябва да се вземат настройките на издателя в последствие , когато са повече
			// валидация и сертификати
			// TODO - 1. валидация ресурс сървър - за валидност на сървиса,
			// няма регистрация
			// -> специфичен exception -> controller advise - handler за него и гериране на съответния SAML Responce
			// и премахване на сесията

			if (validateOidsEnabled) {
				String serviceOID = providerService.getServiceOID(authnRequest);
				if (!authnValidator.validateResourceOID(serviceOID)) {
					// TODO: throw better exception: Невалиден OID на услугата
					throw new RuntimeException("Невалиден OID на услугата");
				}

				String providerOID = providerService.getProviderOID(authnRequest);
				if (!authnValidator.validateResourceOID(providerOID)) {
					// TODO: throw better exception: Невалиден OID на доставчика на идентичност
					throw new RuntimeException("Невалиден OID на доставчика на идентичност");
				}
			}

			if (validateClaimsEnabled) {
				// TODO: validates claims attributes
				log.info("validating claims...");
			}

			SAMLMessageInfoContext messageInfoContext = decoder.getMessageContext()
					.getSubcontext(SAMLMessageInfoContext.class, true);
			messageInfoContext.setMessageIssueInstant(authnRequest.getIssueInstant());

			MessageLifetimeSecurityHandler lifetimeSecurityHandler = new MessageLifetimeSecurityHandler();

			// TODO да се сложет верните стойности от конфигурация
			lifetimeSecurityHandler.setClockSkew(1000 * 10);
			lifetimeSecurityHandler.setMessageLifetime(2000 * 10);
			lifetimeSecurityHandler.setRequiredRule(true);

			ReceivedEndpointSecurityHandler receivedEndpointSecurityHandler = new ReceivedEndpointSecurityHandler();
			receivedEndpointSecurityHandler.setHttpServletRequest(request);

			List<MessageHandler<SAMLObject>> handlers = new ArrayList<>();

			handlers.add(lifetimeSecurityHandler);
			handlers.add(receivedEndpointSecurityHandler);

			BasicMessageHandlerChain<SAMLObject> handlerChain = new BasicMessageHandlerChain<>();
			handlerChain.setHandlers(handlers);

			handlerChain.initialize();
			handlerChain.doInvoke(context);

			boolean isAuthnRequestSigned = authnRequest.isSigned();
			log.debug("isAuthnRequestSigned: [{}]", isAuthnRequestSigned);
			
			if (isAuthnRequestSigned) {
				String metadataUrl = authnRequest.getIssuer().getValue();
				log.debug("metadataUrl: [{}]", metadataUrl);
				
				String  entityDescriptorXml = restTemplate.getForObject(metadataUrl, String.class);
				if (StringUtils.isBlank(entityDescriptorXml)) {
					throw new IllegalArgumentException("EntityDescriptor is blank!");
				}
				
				EntityDescriptor entityDescriptor = getEntityDescriptor(entityDescriptorXml);
				List<KeyDescriptor> keyDescriptors = entityDescriptor.getSPSSODescriptor(SAMLConstants.SAML20P_NS).getKeyDescriptors();
				if (keyDescriptors != null) {
					log.debug("keyDescriptors size: [{}]", keyDescriptors.size());
					if (keyDescriptors.isEmpty()) {
						throw new IllegalArgumentException("Empty certificates in metadata: [" + metadataUrl + "]");
					}
					
					for (KeyDescriptor keyDesc : keyDescriptors) {
						UsageType usageType = keyDesc.getUse();
						log.debug("key desc use: [{}]", usageType);
						
						if (usageType == UsageType.SIGNING) {
							Optional<X509Certificate> cert = keyDesc.getKeyInfo().getX509Datas()
									.stream()
									.findFirst()
									.map(x509Data -> x509Data.getX509Certificates()
											.stream()
											.findFirst()
											.orElse(null)
									);
							
							if (cert.isPresent()) {
								SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();

								// very important for validation!!!
								authnRequest.getDOM().setIdAttribute("ID", true);

								profileValidator.validate(authnRequest.getSignature());

								String lexicalXSDBase64Binary = cert.get().getValue();
								log.debug("lexicalXSDBase64Binary: [{}]", lexicalXSDBase64Binary);
								
								byte[] decoded = DatatypeConverter.parseBase64Binary(lexicalXSDBase64Binary);
								log.debug("decoded: [{}]", Base64.getEncoder().encodeToString(decoded));

								// getCredencialFromFile("certificates/certificate.crt")
								BasicX509Credential credential = getCredentialFromMetadata(decoded);

								// resolveCredential(idpConfiguration.getEntityId())
								SignatureValidator.validate(authnRequest.getSignature(), credential);
							}
						}
					}
				}
				
			}
			
			return decoder.getMessageContext();

		} finally {
			decoder.destroy();
		}

	}


	private SAMLMessageDecoder samlMessageDecoder(boolean postRequest) {

		return postRequest ? new HTTPPostDecoder() : new HTTPRedirectDeflateDecoder();
	}

	
	@SuppressWarnings("unchecked")
	public void sendAuthnResponse(AuthnRequest authnRequest, SAMLPrincipal principal, HttpServletResponse response)
			throws MarshallingException, SignatureException, ComponentInitializationException, MessageEncodingException, MessageHandlerException, EncryptionException, CertificateException, ParserConfigurationException, SAXException, IOException, UnmarshallingException {

		final HTTPPostEncoder encoder = new HTTPPostEncoder();
		Response authResponse = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Response.class);

		Status status = SAMLBuilder.buildStatus(StatusCode.SUCCESS);

		String entityId = idpConfiguration.getEntityId();
		Credential signingCredential = resolveCredential(entityId);

		Issuer issuer = SAMLBuilder.buildIssuer(entityId);

		authResponse.setIssuer(issuer);
		authResponse.setID(SAMLBuilder.randomSAMLId());
		authResponse.setIssueInstant(new DateTime());
		authResponse.setInResponseTo(principal.getRequestID());

		Assertion assertion = SAMLBuilder.buildAssertion(principal, status, entityId);

		SAMLBuilder.signAssertion(assertion, signingCredential);
		
		List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
		log.debug("activeProfiles: {}", activeProfiles);
		if (!activeProfiles.contains("prod")) {
			// 1: comment this to encrypt assertions
			authResponse.getAssertions().add(assertion);
		} else {
			// get public cert from SP metadata url to encrypt the assertions
			BasicX509Credential encryptCredential = getEncryptCredential(authnRequest, restTemplate);
			// 2: uncomment this to encrypt assertions
			EncryptedAssertion encryptedAssertion = SAMLBuilder.encryptAssertion(assertion, encryptCredential); // old: signingCredential
			authResponse.getEncryptedAssertions().add(encryptedAssertion);
		}
		
		authResponse.setDestination(principal.getAssertionConsumerServiceURL());

		authResponse.setStatus(status);

		// --------------------
		AssertionConsumerService samlEndpoint = MetadataBuilderFactoryUtil.buildXmlSAMLObject(AssertionConsumerService.class);

		samlEndpoint.setLocation(principal.getAssertionConsumerServiceURL());
		samlEndpoint.setResponseLocation(principal.getAssertionConsumerServiceURL());

		MessageContext<SAMLObject> context = new MessageContext<>();
		context.setMessage(authResponse);

		SAMLBindingSupport.setRelayState(context, principal.getRelayState());

		context.getSubcontext(SAMLPeerEntityContext.class, true)
				.getSubcontext(SAMLEndpointContext.class, true)
				.setEndpoint(samlEndpoint);

		MessageHandler<SAMLObject> handler = new SAMLOutboundDestinationHandler();
		handler.invoke(context);

		encoder.setMessageContext(context);
		encoder.setHttpServletResponse(response);

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.init();

		encoder.setVelocityEngine(velocityEngine);

		encoder.initialize();
		encoder.prepareContext();
		encoder.encode();

		/*
		 * AuditEvent
		 */
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.principal(principal.getAssertionConsumerServiceURL())
				.type(EventTypes.SAML_AUTHENTICATION_SUCCESS)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		applicationEventPublisher.publishEvent(auditApplicationEvent);

	}


	private Credential resolveCredential(String entityId) {
		try {
			return keyManager.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityId)));
		} catch (ResolverException e) {
			throw new RuntimeException(e);
		}
	}


	@SuppressWarnings("unchecked")
	public void sendCancelAuthnResponse(String statusMessage, SAMLPrincipal principal, HttpServletResponse response)
			throws MarshallingException, SignatureException, ComponentInitializationException, MessageEncodingException, MessageHandlerException, EncryptionException, CertificateException, ParserConfigurationException, SAXException, IOException, UnmarshallingException {

		final HTTPPostEncoder encoder = new HTTPPostEncoder();
		Response authResponse = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Response.class);

		Status status = SAMLBuilder.buildStatus(StatusCode.RESPONDER, StatusCode.AUTHN_FAILED, statusMessage);

		String entityId = idpConfiguration.getEntityId();
		Credential signingCredential = resolveCredential(entityId);

		Issuer issuer = SAMLBuilder.buildIssuer(entityId);

		authResponse.setIssuer(issuer);
		authResponse.setID(SAMLBuilder.randomSAMLId());
		authResponse.setIssueInstant(new DateTime());
		authResponse.setInResponseTo(principal.getRequestID());

		Assertion assertion = SAMLBuilder.buildAssertion(principal, status, entityId);

		SAMLBuilder.signAssertion(assertion, signingCredential);
		
		List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
		log.debug("activeProfiles: {}", activeProfiles);
		if (!activeProfiles.contains("prod")) {
			// 1: comment this to encrypt assertions
			authResponse.getAssertions().add(assertion);
		} else {
			// get public cert from SP metadata url to encrypt the assertions
			BasicX509Credential encryptCredential = getEncryptCredential(providerService.getAuthnRequest(), restTemplate);
			// 2: uncomment this to encrypt assertions
			EncryptedAssertion encryptedAssertion = SAMLBuilder.encryptAssertion(assertion, encryptCredential); // old: signingCredential
			authResponse.getEncryptedAssertions().add(encryptedAssertion);
		}

		authResponse.setDestination(principal.getAssertionConsumerServiceURL());

		authResponse.setStatus(status);

		// --------------------
		AssertionConsumerService samlEndpoint = MetadataBuilderFactoryUtil.buildXmlSAMLObject(AssertionConsumerService.class);

		samlEndpoint.setLocation(principal.getAssertionConsumerServiceURL());
		samlEndpoint.setResponseLocation(principal.getAssertionConsumerServiceURL());

		MessageContext<SAMLObject> context = new MessageContext<>();
		context.setMessage(authResponse);

		SAMLBindingSupport.setRelayState(context, principal.getRelayState());

		context.getSubcontext(SAMLPeerEntityContext.class, true)
				.getSubcontext(SAMLEndpointContext.class, true)
				.setEndpoint(samlEndpoint);

		MessageHandler<SAMLObject> handler = new SAMLOutboundDestinationHandler();
		handler.invoke(context);

		encoder.setMessageContext(context);
		encoder.setHttpServletResponse(response);

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.init();

		encoder.setVelocityEngine(velocityEngine);

		encoder.initialize();
		encoder.prepareContext();
		encoder.encode();

		/*
		 * AuditEvent
		 */
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.principal(principal.getName())
				.type(EventTypes.SAML_AUTHENTICATION_FAILED)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		applicationEventPublisher.publishEvent(auditApplicationEvent);
	}

	private static EntityDescriptor getEntityDescriptor(String entityDescriptorXml)
			throws ParserConfigurationException, SAXException, IOException, UnmarshallingException {
		ByteArrayInputStream is = new ByteArrayInputStream(entityDescriptorXml.getBytes(StandardCharsets.UTF_8));

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = docBuilder.parse(is);
		Element element = document.getDocumentElement();

		EntityDescriptor entityDescriptor = (EntityDescriptor) Objects
				.requireNonNull(XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element))
				.unmarshall(element);

		return entityDescriptor;
	}
	
	private static BasicX509Credential getCredentialFromMetadata(byte[] decoded) throws CertificateException {
		CertificateFactory  certFactory = CertificateFactory.getInstance("X.509");
		java.security.cert.X509Certificate certificate = (java.security.cert.X509Certificate) certFactory
				.generateCertificate(new ByteArrayInputStream(decoded));

		log.debug("cert type: [{}]", certificate.getType());
		log.debug("cert pub key alg: [{}]", certificate.getPublicKey().getAlgorithm());
		log.debug("cert pub key format: [{}]", certificate.getPublicKey().getFormat());
					
		BasicX509Credential credential = new BasicX509Credential(certificate);
		// credential.setEntityId("http://bul-si.bg/sp");
		
		return credential;
	}
	
	private static BasicX509Credential getCredencialFromFile(String filePath) {
		log.debug("get credential from file: [{}]", filePath);
		try {
			InputStream in = new ClassPathResource(filePath).getInputStream();
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) factory
					.generateCertificate(in);
			
			cert.checkValidity();
			
			log.debug("cert type: [{}]", cert.getType());
			
			String certStr = Base64.getEncoder().encodeToString(cert.getEncoded());
			log.debug("cert as string: [{}]", certStr);
			
			PublicKey pubKey = cert.getPublicKey();
			log.debug("pub key alg: [{}]", pubKey.getAlgorithm());
			log.debug("pub key format: [{}]", pubKey.getFormat());
			
			BasicX509Credential credential = CredentialSupport.getSimpleCredential(cert, null);
			log.debug("credential usage type: [{}]", credential.getUsageType());
			
			// credential.setEntityId("http://bul-si.bg/sp");
			
			return credential;
		} catch (CertificateException | IOException ex) {
			log.error(ex.getMessage());
		} 
		return null;
	}
	
	private static BasicX509Credential getEncryptCredential(AuthnRequest authnRequest, RestTemplate restTemplate) 
			throws ParserConfigurationException, SAXException, IOException, UnmarshallingException, CertificateException {
		BasicX509Credential credential = null;
		
		boolean isAuthnRequestSigned = authnRequest.isSigned();
		log.debug("isAuthnRequestSigned: [{}]", isAuthnRequestSigned);
		
		if (isAuthnRequestSigned) {
			String metadataUrl = authnRequest.getIssuer().getValue();
			log.debug("metadataUrl: [{}]", metadataUrl);
		
			String  entityDescriptorXml = restTemplate.getForObject(metadataUrl, String.class);
			if (StringUtils.isBlank(entityDescriptorXml)) {
				throw new IllegalArgumentException("EntityDescriptor is blank!");
			}
			
			EntityDescriptor entityDescriptor = getEntityDescriptor(entityDescriptorXml);
			List<KeyDescriptor> keyDescriptors = entityDescriptor.getSPSSODescriptor(SAMLConstants.SAML20P_NS).getKeyDescriptors();
			if (keyDescriptors != null) {
				log.debug("keyDescriptors size: [{}]", keyDescriptors.size());
				
				for (KeyDescriptor keyDesc : keyDescriptors) {
					UsageType usageType = keyDesc.getUse();
					log.debug("key desc use: [{}]", usageType);
					
					if (usageType == UsageType.ENCRYPTION) {
						Optional<X509Certificate> cert = keyDesc.getKeyInfo().getX509Datas()
								.stream()
								.findFirst()
								.map(x509Data -> x509Data.getX509Certificates()
										.stream()
										.findFirst()
										.orElse(null)
								);
						
						if (cert.isPresent()) {
							String lexicalXSDBase64Binary = cert.get().getValue();
							log.debug("lexicalXSDBase64Binary: [{}]", lexicalXSDBase64Binary);
							
							byte[] decoded = DatatypeConverter.parseBase64Binary(lexicalXSDBase64Binary);
							log.debug("decoded: [{}]", Base64.getEncoder().encodeToString(decoded));

							credential = getCredentialFromMetadata(decoded);
						}
					}
				}
			}
		}
			
		return credential;
	}

}
