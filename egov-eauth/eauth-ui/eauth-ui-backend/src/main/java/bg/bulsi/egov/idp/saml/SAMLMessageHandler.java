package bg.bulsi.egov.idp.saml;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.joda.time.DateTime;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.io.MarshallingException;
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
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPPostDecoder;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPRedirectDeflateDecoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.common.xml.MetadataBuilderFactoryUtil;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.saml.OpenSamlImplementation;
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
@Component
@Slf4j
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

//	 @Value("${validate.authn.oids}")
	private boolean validateOidsEnabled = false;

//	 @Value("${validate.authn.claims}")
	private boolean validateClaimsEnabled = true;

	@Autowired
	public SAMLMessageHandler(
			KeyManager keyManager,
//			Collection<SAMLMessageDecoder> decoders,
//			SAMLMessageEncoder encoder,
//			SecurityPolicyResolver securityPolicyResolver,
			IdpConfigurationProperties idpConfiguration,
			ApplicationEventPublisher applicationEventPublisher,
			AuthnRequestValidator authnValidator,
			ProviderService providerService
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
	}


	@SuppressWarnings("unchecked")
	public MessageContext<SAMLObject> extractSAMLMessageContext(
			HttpServletRequest request,
			HttpServletResponse response, boolean isPostRequest)
			throws ValidationException, SecurityException, MessageDecodingException, ComponentInitializationException, MessageHandlerException, SignatureException {

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

			if (authnRequest.isSigned()) {
				SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
				profileValidator.validate(authnRequest.getSignature());

				SignatureValidator
						.validate(authnRequest.getSignature(),
								resolveCredential(idpConfiguration.getEntityId()));
			}

			return decoder.getMessageContext();

		} finally {
			decoder.destroy();
		}

	}


	private SAMLMessageDecoder samlMessageDecoder(boolean postRequest) {

		return postRequest ? new HTTPPostDecoder() : new HTTPRedirectDeflateDecoder();
	}


	public void sendAuthnResponse(SAMLPrincipal principal, HttpServletResponse response)
			throws MarshallingException, SignatureException, ComponentInitializationException, MessageEncodingException, MessageHandlerException, EncryptionException {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();

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
		authResponse.getAssertions().add(assertion); // 1: comment this to encrypt assertions

		// 2: uncomment this to encrypt assertions
		/*
		 * EncryptedAssertion as = SAMLBuilder.encryptAssertion(assertion, signingCredential);
		 * authResponse.getEncryptedAssertions().add(as);
		 */

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
		{ // TODO remove test
			log.info("Principal getAssertionConsumerServiceURL {}", principal.getAssertionConsumerServiceURL());
			log.info("Principal getName {}", principal.getName());
			log.info("Principal getNameID {}", principal.getNameID());
			log.info("Principal getServiceProviderEntityID {}", principal.getServiceProviderEntityID());
		}
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


	public void sendCancelAuthnResponse(String statusMessage, SAMLPrincipal principal, HttpServletResponse response)
			throws MarshallingException, SignatureException, ComponentInitializationException, MessageEncodingException, MessageHandlerException, EncryptionException {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();

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
		authResponse.getAssertions().add(assertion); // 1: comment this to encrypt assertions

		// 2: uncomment this to encrypt assertions
		/*
		 * EncryptedAssertion as = SAMLBuilder.encryptAssertion(assertion, signingCredential);
		 * authResponse.getEncryptedAssertions().add(as);
		 */

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

}
